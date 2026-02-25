const dropZone = document.getElementById('drop-zone');
const fileInput = document.getElementById('file-input');

const views = {
    prompt:    document.getElementById('view-prompt'),
    selected:  document.getElementById('view-selected'),
    uploading: document.getElementById('view-uploading'),
    result:    document.getElementById('view-result'),
};

let selectedFile = null;

function showView(name) {
    Object.entries(views).forEach(([key, el]) => el.classList.toggle('d-none', key !== name));
}

// ── File selection ────────────────────────────────────────────────────────────

dropZone.addEventListener('click', () => {
    if (views.prompt.classList.contains('d-none')) return;
    fileInput.click();
});

fileInput.addEventListener('change', e => {
    if (e.target.files[0]) selectFile(e.target.files[0]);
});

dropZone.addEventListener('dragover', e => {
    e.preventDefault();
    dropZone.classList.add('dragover');
});

dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));

dropZone.addEventListener('drop', e => {
    e.preventDefault();
    dropZone.classList.remove('dragover');
    if (e.dataTransfer.files[0]) selectFile(e.dataTransfer.files[0]);
});

function selectFile(file) {
    selectedFile = file;
    document.getElementById('selected-name').textContent = file.name;
    showView('selected');
}

document.getElementById('reset-btn').addEventListener('click', e => {
    e.stopPropagation();
    selectedFile = null;
    fileInput.value = '';
    showView('prompt');
});

document.getElementById('new-upload-btn').addEventListener('click', e => {
    e.stopPropagation();
    selectedFile = null;
    fileInput.value = '';
    showView('prompt');
});

// ── Upload ────────────────────────────────────────────────────────────────────

document.getElementById('upload-btn').addEventListener('click', async e => {
    e.stopPropagation();
    await upload();
});

function setStatus(msg) {
    document.getElementById('upload-status').textContent = msg;
}

async function upload() {
    const file = selectedFile;
    showView('uploading');

    try {
        setStatus('Generating encryption key\u2026');
        const key = await crypto.subtle.generateKey(
            { name: 'AES-GCM', length: 256 },
            true,
            ['encrypt', 'decrypt']
        );

        const iv = crypto.getRandomValues(new Uint8Array(12));

        setStatus('Encrypting\u2026');
        const ciphertext = await crypto.subtle.encrypt(
            { name: 'AES-GCM', iv },
            key,
            await file.arrayBuffer()
        );

        // Payload: 12-byte IV prepended to ciphertext
        const payload = new Uint8Array(12 + ciphertext.byteLength);
        payload.set(iv, 0);
        payload.set(new Uint8Array(ciphertext), 12);

        // SHA-256(rawKey) → file identifier sent to server (server never sees the key)
        const rawKey = await crypto.subtle.exportKey('raw', key);
        const hash = Array.from(new Uint8Array(await crypto.subtle.digest('SHA-256', rawKey)))
            .map(b => b.toString(16).padStart(2, '0'))
            .join('');

        // Base64url-encode key for URL fragment
        const base64urlKey = btoa(String.fromCharCode(...new Uint8Array(rawKey)))
            .replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');

        setStatus('Uploading\u2026');
        const formData = new FormData();
        formData.append('file', new Blob([payload]), file.name);
        formData.append('hash', hash);
        formData.append('name', file.name);

        const response = await fetch('/upload', { method: 'POST', body: formData });
        if (!response.ok) throw new Error(`Server responded with ${response.status}`);

        const { id } = await response.json();
        document.getElementById('share-link').value =
            `${window.location.origin}/download/${id}#${base64urlKey}`;
        showView('result');

    } catch (err) {
        setStatus(`Error: ${err.message}`);
    }
}

// ── Copy link ─────────────────────────────────────────────────────────────────

document.getElementById('copy-btn').addEventListener('click', async e => {
    e.stopPropagation();
    await navigator.clipboard.writeText(document.getElementById('share-link').value);
    const btn = document.getElementById('copy-btn');
    btn.textContent = 'Copied!';
    setTimeout(() => { btn.textContent = 'Copy'; }, 2000);
});
