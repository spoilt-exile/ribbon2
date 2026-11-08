package tk.freaxsoftware.ribbon2.uix;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.StringWriter;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tk.freaxsoftware.ribbon2.io.core.ConfigEntry;
import tk.freaxsoftware.ribbon2.io.core.ConfigEntryType;
import tk.freaxsoftware.ribbon2.io.core.IOScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.uix.model.io.IOProtocol;
import tk.freaxsoftware.ribbon2.uix.model.io.IOSchemeEditorModel;

/** Smoke tests for admin templates and their model contracts. */
public class AdminTemplateRenderTest {

    private Configuration templates;

    @Before
    public void setUp() throws Exception {
        templates = new Configuration(Configuration.VERSION_2_3_32);
        File webDirectory = new File("web");
        if (!webDirectory.isDirectory()) webDirectory = new File("uix/web");
        templates.setDirectoryForTemplateLoading(webDirectory);
        templates.setDefaultEncoding("UTF-8");
        templates.setOutputEncoding("UTF-8");
        templates.setURLEscapingCharset("UTF-8");
        templates.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        templates.setLogTemplateExceptions(false);
        templates.setWrapUncheckedExceptions(true);
    }

    @Test
    public void rendersDarkAdminShellAndEmptySchemeState() throws Exception {
        Map<String, Object> model = new HashMap<>();
        model.put("user", Map.of("login", "admin"));
        model.put("directories", Map.of("directoryChildren", List.of()));
        model.put("schemes", List.of());
        model.put("protocols", List.of());
        String rendered = render("admin.html", model);
        Assert.assertTrue(rendered.contains("data-bs-theme=\"dark\""));
        Assert.assertTrue(rendered.contains("Directories"));
        Assert.assertTrue(rendered.contains("IO schemes"));
        Assert.assertTrue(rendered.contains("No IO schemes are currently registered."));
    }

    @Test
    public void rendersInheritedAndCustomEmptyDirectorySecurityStates() throws Exception {
        Map<String, Object> inherited = directoryModel("INHERIT", null, "System");
        String inheritedHtml = render("admin-directory-detail.html", inherited);
        Assert.assertTrue(inheritedHtml.contains("Inherited from <strong>System</strong>"));
        Assert.assertTrue(inheritedHtml.contains("Inherit from parent"));

        Map<String, Object> customEmpty = directoryModel("CUSTOM", List.of(), "System.Child");
        String customHtml = render("admin-directory-detail.html", customEmpty);
        Assert.assertTrue(customHtml.contains("Custom local rules (empty ruleset)"));
        Assert.assertTrue(customHtml.contains("No export schemes are assigned to this directory."));
    }

    @Test
    public void rendersMetadataDrivenSchemeEditorWithoutSecretValue() throws Exception {
        ConfigEntry password = new ConfigEntry("mailPassword", ConfigEntryType.SECRET, true, null);
        IOProtocol protocol = new IOProtocol().setType(ModuleType.IMPORT).setProtocol("mail")
                .setConfigEntries(List.of(password));
        IOScheme source = new IOScheme();
        source.setName("mail");
        source.setType(ModuleType.IMPORT);
        source.setProtocol("mail");
        source.setConfig(Map.of("mailPassword", "not-rendered-secret"));
        IOSchemeEditorModel editor = new IOSchemeEditorModel(protocol, source);
        Map<String, Object> model = new HashMap<>();
        model.put("editor", editor);
        model.put("editing", true);
        model.put("selectedType", "IMPORT");
        model.put("selectedProtocol", "mail");
        model.put("directoryPaths", List.of());
        model.put("selectedDirectoryLists", Map.of());
        String rendered = render("admin-io-editor.html", model);
        Assert.assertTrue(rendered.contains("Configured; leave blank to keep current value"));
        Assert.assertTrue(rendered.contains("type=\"password\""));
        Assert.assertFalse(rendered.contains("not-rendered-secret"));
    }

    private Map<String, Object> directoryModel(String mode, Object entries, String source) {
        Map<String, Object> security = new HashMap<>();
        security.put("accessMode", mode);
        security.put("accessEntries", entries);
        security.put("accessSourcePath", source);
        Map<String, Object> model = new HashMap<>();
        model.put("directory", Map.of("fullName", "System.Child", "name", "Child", "description", ""));
        model.put("security", security);
        model.put("securityError", null);
        model.put("ruleRows", List.of());
        model.put("permissions", List.of());
        model.put("users", List.of());
        model.put("groups", List.of());
        model.put("assigned", List.of());
        model.put("exportSchemes", List.of());
        return model;
    }

    private String render(String name, Object model) throws Exception {
        Template template = templates.getTemplate(name);
        StringWriter output = new StringWriter();
        template.process(model, output);
        return output.toString();
    }
}
