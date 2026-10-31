(() => {
    const directoryPane = document.getElementById('dirs');
    if (!directoryPane) return;

    let selectedDirectory = null;

    const showSelection = () => {
        directoryPane.querySelectorAll('.directory-link').forEach((link) => {
            const selected = link.dataset.directory === selectedDirectory;
            link.setAttribute('aria-current', selected ? 'true' : 'false');
        });
    };

    directoryPane.addEventListener('click', (event) => {
        const link = event.target.closest('.directory-link');
        if (!link || !directoryPane.contains(link)) return;
        selectedDirectory = link.dataset.directory;
        showSelection();
    });

    document.body.addEventListener('htmx:afterSwap', (event) => {
        if (event.detail.target === directoryPane) showSelection();
    });
})();
