(() => {
    let editorWindow = null;

    document.addEventListener('click', (event) => {
        const link = event.target.closest('[data-editor-dialog]');
        if (!link || event.defaultPrevented || event.button !== 0 || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;

        event.preventDefault();
        editorWindow = window.open(
            link.href,
            'ribbon2-message-editor',
            'popup=yes,width=1000,height=850,resizable=yes,scrollbars=yes'
        );
        if (editorWindow) {
            editorWindow.focus();
        } else {
            window.location.assign(link.href);
        }
    });

    window.addEventListener('message', (event) => {
        if (event.origin !== window.location.origin || event.source !== editorWindow) return;
        if (event.data?.type === 'editor-saved') {
            editorWindow = null;
            htmx.trigger(document.body, 'messageDeleted');
        } else if (event.data?.type === 'editor-cancelled') {
            editorWindow = null;
        }
    });
})();
