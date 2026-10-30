(() => {
    const workspace = document.getElementById('workspace');
    const rightStack = workspace?.querySelector('.right-stack');
    if (!workspace || !rightStack) return;

    const columnSplitter = workspace.querySelector('[data-resize="columns"]');
    const rowSplitter = workspace.querySelector('[data-resize="rows"]');

    const setColumns = (pixels) => {
        const max = Math.max(208, workspace.clientWidth - 280);
        const width = Math.min(max, Math.max(208, pixels));
        workspace.style.setProperty('--directory-width', `${width}px`);
        columnSplitter.setAttribute('aria-valuenow', String(Math.round(width)));
    };

    const setRows = (percent) => {
        const height = Math.min(80, Math.max(20, percent));
        rightStack.style.setProperty('--message-height', `${height}%`);
        rowSplitter.setAttribute('aria-valuenow', String(Math.round(height)));
    };

    const attachPointerResize = (splitter, begin, move) => {
        splitter.addEventListener('pointerdown', (event) => {
            if (event.button !== 0) return;
            event.preventDefault();
            const start = begin(event);
            splitter.classList.add('is-dragging');
            splitter.setPointerCapture(event.pointerId);
            const onMove = (moveEvent) => move(moveEvent, start);
            const finish = () => {
                splitter.classList.remove('is-dragging');
                splitter.removeEventListener('pointermove', onMove);
                splitter.removeEventListener('pointerup', finish);
                splitter.removeEventListener('pointercancel', finish);
            };
            splitter.addEventListener('pointermove', onMove);
            splitter.addEventListener('pointerup', finish);
            splitter.addEventListener('pointercancel', finish);
        });
    };

    attachPointerResize(columnSplitter,
        (event) => ({ x: event.clientX, width: workspace.querySelector('.pane-directories').getBoundingClientRect().width }),
        (event, start) => setColumns(start.width + event.clientX - start.x));

    attachPointerResize(rowSplitter,
        (event) => ({ y: event.clientY, percent: rightStack.querySelector('.pane-messages').getBoundingClientRect().height / rightStack.clientHeight * 100 }),
        (event, start) => setRows(start.percent + (event.clientY - start.y) / rightStack.clientHeight * 100));

    columnSplitter.addEventListener('keydown', (event) => {
        if (event.key !== 'ArrowLeft' && event.key !== 'ArrowRight') return;
        event.preventDefault();
        const width = workspace.querySelector('.pane-directories').getBoundingClientRect().width;
        setColumns(width + (event.key === 'ArrowRight' ? 16 : -16));
    });

    rowSplitter.addEventListener('keydown', (event) => {
        if (event.key !== 'ArrowUp' && event.key !== 'ArrowDown') return;
        event.preventDefault();
        const percent = rightStack.querySelector('.pane-messages').getBoundingClientRect().height / rightStack.clientHeight * 100;
        setRows(percent + (event.key === 'ArrowDown' ? 2 : -2));
    });
})();
