const fragment = location.hash.slice(1);
if (!fragment) {
    showError('No decryption key found in URL.');
} else try {
    setStatus('Deriving key\u2026');

    const rawKeyBytes = decodeKey(fragment);
    const id = await hashKey(rawKeyBytes);

    const key = await crypto.subtle.importKey(
        'raw', rawKeyBytes, { name: 'AES-GCM' }, false, ['decrypt']
    );

    setStatus('Downloading\u2026');
    const response = await fetch('/download/' + id + '/data');

    if (response.status === 410) {
        showError('This file has expired or reached its download limit.');
    } else if (!response.ok) {
        showError(`Download failed (${response.status}).`);
    } else {
        const disposition = response.headers.get('Content-Disposition') || '';
        const filename = disposition.match(/filename="?([^"]+)"?/)?.[1] || 'download';

        setStatus('Decrypting\u2026');
        const plaintext = await decryptFile(await response.arrayBuffer(), key);

        const url = URL.createObjectURL(new Blob([plaintext]));
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;

        htmx.swap(htmx.find('#download-state'), `
            <div class="drop-zone-icon mb-3">&#x1F512;</div>
            <div class="fw-medium mb-2">${filename}</div>
            <button id="download-btn" class="btn btn-outline-success mt-2">Download</button>`,
            { swapStyle: 'innerHTML' });

        htmx.on(htmx.find('#download-btn'), 'click', () => {
            a.click();
            URL.revokeObjectURL(url);
            showSuccess(filename);
        });
    }
} catch (err) {
    showError('Decryption failed: ' + err.message);
}

function setStatus(msg) {
    const el = htmx.find('#download-status');
    if (el) el.textContent = msg;
}

function showSuccess(filename) {
    htmx.swap(htmx.find('#download-state'), `
        <div class="drop-zone-icon mb-3">&#x2705;</div>
        <div class="fw-medium mb-1">${filename}</div>
        <div class="drop-zone-text mb-3">Your download has started.</div>
        <a href="/" class="btn btn-link drop-zone-text text-decoration-none">Send a file</a>`,
        { swapStyle: 'innerHTML' });
}

function showError(msg) {
    htmx.swap(htmx.find('#download-state'), `
        <div class="drop-zone-icon mb-3">&#x26A0;</div>
        <div class="drop-zone-text mb-3">${msg}</div>
        <a href="/" class="btn btn-link drop-zone-text text-decoration-none">Go home</a>`,
        { swapStyle: 'innerHTML' });
}
