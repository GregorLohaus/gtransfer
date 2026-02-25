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

        showPreview(filename, plaintext);
    }
} catch (err) {
    showError('Decryption failed: ' + err.message);
}

function setStatus(msg) {
    const el = htmx.find('#download-status');
    if (el) el.textContent = msg;
}

function escapeHtml(str) {
    return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function getMimeType(filename) {
    const ext = filename.split('.').pop().toLowerCase();
    const types = {
        jpg: 'image/jpeg', jpeg: 'image/jpeg', png: 'image/png', gif: 'image/gif',
        webp: 'image/webp', svg: 'image/svg+xml', bmp: 'image/bmp', ico: 'image/x-icon',
        mp4: 'video/mp4', webm: 'video/webm', ogv: 'video/ogg', mov: 'video/quicktime',
        mp3: 'audio/mpeg', wav: 'audio/wav', ogg: 'audio/ogg', flac: 'audio/flac',
        aac: 'audio/aac', m4a: 'audio/mp4',
        txt: 'text/plain', md: 'text/plain', csv: 'text/csv', log: 'text/plain',
        json: 'application/json', xml: 'text/xml',
        js: 'text/plain', ts: 'text/plain', py: 'text/plain', java: 'text/plain',
        c: 'text/plain', cpp: 'text/plain', h: 'text/plain', sh: 'text/plain',
        yaml: 'text/plain', yml: 'text/plain', toml: 'text/plain', ini: 'text/plain',
        pdf: 'application/pdf',
    };
    return types[ext] || 'application/octet-stream';
}

function showPreview(filename, plaintext) {
    const mimeType = getMimeType(filename);
    const blob = new Blob([plaintext], { type: mimeType });
    const url = URL.createObjectURL(blob);
    window.addEventListener('unload', () => URL.revokeObjectURL(url));

    const name = escapeHtml(filename);
    const downloadBtn = `<button id="download-btn" class="btn btn-outline-success mt-2">Download</button>`;
    let previewHtml;

    if (mimeType.startsWith('image/')) {
        previewHtml = `
            <img src="${url}" alt="${name}" class="preview-media mb-3">
            <div class="fw-medium mb-2">${name}</div>
            ${downloadBtn}`;
    } else if (mimeType.startsWith('video/')) {
        previewHtml = `
            <video src="${url}" controls class="preview-media mb-3"></video>
            <div class="fw-medium mb-2">${name}</div>
            ${downloadBtn}`;
    } else if (mimeType.startsWith('audio/')) {
        previewHtml = `
            <div class="drop-zone-icon mb-3">&#x1F3B5;</div>
            <div class="fw-medium mb-2">${name}</div>
            <audio src="${url}" controls class="preview-audio mb-3"></audio>
            ${downloadBtn}`;
    } else if (mimeType === 'application/pdf') {
        previewHtml = `
            <div class="fw-medium mb-2">${name}</div>
            <iframe src="${url}" class="preview-pdf mb-3"></iframe>
            ${downloadBtn}`;
    } else if (mimeType.startsWith('text/') || mimeType === 'application/json') {
        const text = new TextDecoder().decode(plaintext);
        const preview = text.length > 10000 ? text.slice(0, 10000) + '\n\u2026' : text;
        previewHtml = `
            <div class="fw-medium mb-2">${name}</div>
            <pre class="preview-text mb-3">${escapeHtml(preview)}</pre>
            ${downloadBtn}`;
    } else {
        previewHtml = `
            <div class="drop-zone-icon mb-3">&#x1F4C4;</div>
            <div class="fw-medium mb-2">${name}</div>
            ${downloadBtn}`;
    }

    htmx.swap(htmx.find('#download-state'), previewHtml, { swapStyle: 'innerHTML' });

    htmx.on(htmx.find('#download-btn'), 'click', () => {
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();
        showSuccess(filename);
    });
}

function showSuccess(filename) {
    const name = escapeHtml(filename);
    htmx.swap(htmx.find('#download-state'), `
        <div class="drop-zone-icon mb-3">&#x2705;</div>
        <div class="fw-medium mb-1">${name}</div>
        <div class="drop-zone-text mb-3">Your download has started.</div>
        <a href="/" class="btn btn-link drop-zone-text text-decoration-none">Send a file</a>`,
        { swapStyle: 'innerHTML' });
}

function showError(msg) {
    htmx.swap(htmx.find('#download-state'), `
        <div class="drop-zone-icon mb-3">&#x26A0;</div>
        <div class="drop-zone-text mb-3">${escapeHtml(msg)}</div>
        <a href="/" class="btn btn-link drop-zone-text text-decoration-none">Go home</a>`,
        { swapStyle: 'innerHTML' });
}
