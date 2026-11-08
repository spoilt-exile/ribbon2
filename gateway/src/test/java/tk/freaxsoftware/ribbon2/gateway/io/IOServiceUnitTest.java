package tk.freaxsoftware.ribbon2.gateway.io;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tk.freaxsoftware.ribbon2.gateway.io.data.IOModuleScheme;
import tk.freaxsoftware.ribbon2.io.core.ModuleRegistration;
import tk.freaxsoftware.ribbon2.io.core.ModuleType;
import tk.freaxsoftware.ribbon2.io.core.SchemeInstance;

/** Tests export scheme lookup against current module registration state. */
public class IOServiceUnitTest {

    private final IOService service = IOService.getInstance();

    @Before
    public void clearRegistrations() {
        service.getRegistrations().clear();
    }

    @After
    public void removeRegistrations() {
        service.getRegistrations().clear();
    }

    @Test
    public void reflectsExportAssignmentChangesAndSkipsImportSchemes() {
        SchemeInstance export = new SchemeInstance(false, new HashSet<>());
        Map<String, SchemeInstance> exportSchemes = new HashMap<>();
        exportSchemes.put("publisher", export);
        service.getRegistrations().add(new ModuleRegistration("export-id", ModuleType.EXPORT,
                "protocol-x", null, exportSchemes));

        Map<String, SchemeInstance> importSchemes = new HashMap<>();
        importSchemes.put("reader", new SchemeInstance(false, new HashSet<>(java.util.Set.of("System.News"))));
        service.getRegistrations().add(new ModuleRegistration("import-id", ModuleType.IMPORT,
                "protocol-y", null, importSchemes));

        Assert.assertTrue(service.getSchemesByExportDirectiry("System.News").isEmpty());
        export.getExportDirectories().add("System.News");
        var assigned = service.getSchemesByExportDirectiry("System.News");
        Assert.assertEquals(1, assigned.size());
        IOModuleScheme scheme = assigned.iterator().next();
        Assert.assertEquals("publisher", scheme.getScheme());
        Assert.assertEquals("protocol-x", scheme.getProtocol());

        export.getExportDirectories().clear();
        Assert.assertTrue(service.getSchemesByExportDirectiry("System.News").isEmpty());
    }
}
