const dropZone = htmx.find('#drop-zone');
const fileInput = htmx.find('#file-input');
const promptHtml = dropZone.innerHTML;

let selectedFile = null;

htmx.on(dropZone, 'click', () => {
    if (selectedFile === null) fileInput.click();
});

htmx.on(fileInput, 'change', e => {
    if (e.target.files[0]) onFileSelected(e.target.files[0]);
});

htmx.on(dropZone, 'dragover', e => {
    e.preventDefault();
    htmx.addClass(dropZone, 'dragover');
});

htmx.on(dropZone, 'dragleave', () => htmx.removeClass(dropZone, 'dragover'));

htmx.on(dropZone, 'drop', e => {
    e.preventDefault();
    htmx.removeClass(dropZone, 'dragover');
    if (e.dataTransfer.files[0] && selectedFile === null) onFileSelected(e.dataTransfer.files[0]);
});

function onFileSelected(file) {
    selectedFile = file;
    htmx.ajax('GET', '/upload/options?name=' + encodeURIComponent(file.name), {
        target: '#drop-zone',
        swap: 'innerHTML'
    });
}

async function startUpload() {
    const expiryDays    = htmx.find('#expiry-days')?.value;
    const downloadLimit = htmx.find('#download-limit')?.value;

    htmx.swap(dropZone, `
        <div class="mb-3">
            <div class="spinner-border text-success" role="status">
                <span class="visually-hidden">Loading\u2026</span>
            </div>
        </div>
        <div class="drop-zone-text" id="upload-status">Encrypting\u2026</div>`,
        { swapStyle: 'innerHTML' });

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

        htmx.swap(dropZone, await response.text(), { swapStyle: 'innerHTML' });
        htmx.process(dropZone);
        htmx.find('#share-link').value = window.location.origin + '/download#' + base64urlKey;

    } catch (err) {
        htmx.swap(dropZone, `
            <div class="drop-zone-icon mb-3">&#x26A0;</div>
            <div class="drop-zone-text mb-3">${err.message}</div>
            <button class="btn btn-link drop-zone-text text-decoration-none" onclick="resetUpload()">Try again</button>`,
            { swapStyle: 'innerHTML' });
    }
}

function setStatus(msg) {
    const el = htmx.find('#upload-status');
    if (el) el.textContent = msg;
}

function resetUpload() {
    selectedFile = null;
    fileInput.value = '';
    htmx.swap(dropZone, promptHtml, { swapStyle: 'innerHTML' });
}

async function copyLink() {
    await navigator.clipboard.writeText(htmx.find('#share-link').value);
    const btn = htmx.find('#copy-btn');
    btn.textContent = 'Copied!';
    setTimeout(() => { btn.textContent = 'Copy'; }, 2000);
}
