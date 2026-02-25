(async function () {
    const fragment = location.hash.slice(1);
    if (!fragment) {
        showError('No decryption key found in URL.');
        return;
    }

    try {
        setStatus('Deriving key\u2026');

        // Decode base64url → raw key bytes
        const base64 = fragment.replace(/-/g, '+').replace(/_/g, '/');
        const rawKeyBytes = Uint8Array.from(atob(base64), c => c.charCodeAt(0));

        // Import key
        const key = await crypto.subtle.importKey(
            'raw', rawKeyBytes, { name: 'AES-GCM' }, false, ['decrypt']
        );

        // Derive hash → verify it matches the file ID in the URL
        const hash = Array.from(new Uint8Array(await crypto.subtle.digest('SHA-256', rawKeyBytes)))
            .map(b => b.toString(16).padStart(2, '0'))
            .join('');

        const pathId = location.pathname.split('/').pop();
        if (hash !== pathId) {
            showError('Invalid link — key does not match file.');
            return;
        }

        setStatus('Downloading\u2026');
        const response = await fetch(location.pathname + '/data');

        if (response.status === 410) {
            showError('This file has expired or reached its download limit.');
            return;
        }
        if (!response.ok) {
            showError(`Download failed (${response.status}).`);
            return;
        }

        // Extract filename from Content-Disposition header
        const disposition = response.headers.get('Content-Disposition') || '';
        const filename = disposition.match(/filename="?([^"]+)"?/)?.[1] || 'download';

        setStatus('Decrypting\u2026');
        const encrypted = new Uint8Array(await response.arrayBuffer());
        const iv         = encrypted.slice(0, 12);
        const ciphertext = encrypted.slice(12);

        const plaintext = await crypto.subtle.decrypt({ name: 'AES-GCM', iv }, key, ciphertext);

        // Trigger browser download
        const url = URL.createObjectURL(new Blob([plaintext]));
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        URL.revokeObjectURL(url);

        showSuccess(filename);

    } catch (err) {
        showError('Decryption failed: ' + err.message);
    }
})();

function setStatus(msg) {
    const el = document.getElementById('download-status');
    if (el) el.textContent = msg;
}

function showSuccess(filename) {
    document.getElementById('download-state').innerHTML = `
        <div class="drop-zone-icon mb-3">&#x2705;</div>
        <div class="fw-medium mb-1">${filename}</div>
        <div class="drop-zone-text mb-3">Your download has started.</div>
        <a href="/" class="btn btn-link drop-zone-text text-decoration-none">Send a file</a>`;
}

function showError(msg) {
    document.getElementById('download-state').innerHTML = `
        <div class="drop-zone-icon mb-3">&#x26A0;</div>
        <div class="drop-zone-text mb-3">${msg}</div>
        <a href="/" class="btn btn-link drop-zone-text text-decoration-none">Go home</a>`;
}
