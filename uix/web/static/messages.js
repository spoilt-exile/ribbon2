(() => {
    const messagePane = document.getElementById('messages');
    if (!messagePane) return;

    let selectedUid = null;

    const showSelection = () => {
        messagePane.querySelectorAll('[data-message-uid]').forEach((row) => {
            row.setAttribute('aria-selected', row.dataset.messageUid === selectedUid ? 'true' : 'false');
        });
    };

    messagePane.addEventListener('click', (event) => {
        const row = event.target.closest('[data-message-uid]');
        if (!row || !messagePane.contains(row)) return;
        selectedUid = row.dataset.messageUid;
        showSelection();
    });

    document.body.addEventListener('htmx:afterSwap', (event) => {
        if (messagePane.contains(event.detail.target)) showSelection();
    });
})();
