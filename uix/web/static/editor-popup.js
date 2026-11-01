(() => {
    const opener = window.opener;
    if (!opener || opener.closed) return;

    document.addEventListener('htmx:beforeOnLoad', (event) => {
        if (!event.detail.xhr.getResponseHeader('HX-Redirect')) return;
        event.preventDefault();
        opener.postMessage({ type: 'editor-saved' }, window.location.origin);
        window.close();
    });

    document.addEventListener('click', (event) => {
        const cancel = event.target.closest('[data-editor-cancel]');
        if (!cancel) return;
        event.preventDefault();
        opener.postMessage({ type: 'editor-cancelled' }, window.location.origin);
        window.close();
    });
})();
