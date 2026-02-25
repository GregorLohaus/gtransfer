const dropZone = document.getElementById('drop-zone');
const fileInput = document.getElementById('file-input');
const promptHtml = dropZone.innerHTML;

let selectedFile = null;

// ── File selection ────────────────────────────────────────────────────────────

dropZone.addEventListener('click', () => {
    if (selectedFile === null) fileInput.click();
});

fileInput.addEventListener('change', e => {
    if (e.target.files[0]) onFileSelected(e.target.files[0]);
});

dropZone.addEventListener('dragover', e => {
    e.preventDefault();
    dropZone.classList.add('dragover');
});

dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));

dropZone.addEventListener('drop', e => {
    e.preventDefault();
    dropZone.classList.remove('dragover');
    if (e.dataTransfer.files[0] && selectedFile === null) onFileSelected(e.dataTransfer.files[0]);
});

function onFileSelected(file) {
    selectedFile = file;
    // Use htmx to fetch the options form — server renders max values from config
    htmx.ajax('GET', '/upload/options?name=' + encodeURIComponent(file.name), {
        target: '#drop-zone',
        swap: 'innerHTML'
    });
}

// ── Upload (called from onclick in server-rendered options form) ──────────────

async function startUpload() {
    const expiryDays = document.getElementById('expiry-days')?.value;
    const downloadLimit = document.getElementById('download-limit')?.value;

    dropZone.innerHTML = `
        <div class="mb-3">
            <div class="spinner-border text-success" role="status">
                <span class="visually-hidden">Loading\u2026</span>
            </div>
        </div>
        <div class="drop-zone-text" id="upload-status">Encrypting\u2026</div>`;

    try {
        const { payload, hash, base64urlKey } = await encryptFile(await selectedFile.arrayBuffer());

        setStatus('Uploading\u2026');

        const formData = new FormData();
        formData.append('file', new Blob([payload]), selectedFile.name);
        formData.append('hash', hash);
        formData.append('name', selectedFile.name);
        if (expiryDays)    formData.append('expiryDays', expiryDays);
        if (downloadLimit) formData.append('downloadLimit', downloadLimit);

        const response = await fetch('/upload', { method: 'POST', body: formData });
        if (!response.ok) throw new Error(`Server error ${response.status}`);

        // Server returns HTML fragment; prepend origin and append key fragment client-side
        dropZone.innerHTML = await response.text();
        htmx.process(dropZone);
        const shareLink = document.getElementById('share-link');
        shareLink.value = window.location.origin + shareLink.value + '#' + base64urlKey;

    } catch (err) {
        dropZone.innerHTML = `
            <div class="drop-zone-icon mb-3">&#x26A0;</div>
            <div class="drop-zone-text mb-3">${err.message}</div>
            <button class="btn btn-link drop-zone-text text-decoration-none" onclick="resetUpload()">Try again</button>`;
    }
}

function setStatus(msg) {
    const el = document.getElementById('upload-status');
    if (el) el.textContent = msg;
}

// ── Reset (called from onclick in server-rendered fragments) ──────────────────

function resetUpload() {
    selectedFile = null;
    fileInput.value = '';
    dropZone.innerHTML = promptHtml;
}

// ── Copy link (called from onclick in result fragment) ────────────────────────

async function copyLink() {
    await navigator.clipboard.writeText(document.getElementById('share-link').value);
    const btn = document.getElementById('copy-btn');
    btn.textContent = 'Copied!';
    setTimeout(() => { btn.textContent = 'Copy'; }, 2000);
}
