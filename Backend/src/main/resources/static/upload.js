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
        <div class="drop-zone-text mb-3" id="upload-status">Preparing\u2026</div>
        <div class="progress" role="progressbar" aria-label="Upload progress" aria-valuemin="0" aria-valuemax="100">
            <div id="upload-progress" class="progress-bar bg-success" style="width: 0%">0%</div>
        </div>`,
        { swapStyle: 'innerHTML' });

    try {
        const { key, hash, base64urlKey } = await generateFileKey();
        const chunkCount = Math.max(1, Math.ceil(selectedFile.size / DEFAULT_CHUNK_SIZE));

        for (let index = 0; index < chunkCount; index++) {
            setProgress(`Encrypting chunk ${index + 1} of ${chunkCount}\u2026`, index, chunkCount);
            const payload = await encryptFileChunk(selectedFile, index, key);

            setProgress(`Uploading chunk ${index + 1} of ${chunkCount}\u2026`, index + 0.5, chunkCount);
            const chunkData = new FormData();
            chunkData.append('chunk', new Blob([payload]), String(index));
            chunkData.append('hash', hash);
            chunkData.append('index', index);

            const chunkResponse = await fetch('/upload/chunk', { method: 'POST', body: chunkData });
            if (!chunkResponse.ok) throw new Error(`Chunk upload failed (${chunkResponse.status})`);
            setProgress(`Uploaded chunk ${index + 1} of ${chunkCount}`, index + 1, chunkCount);
        }

        setProgress('Finalizing\u2026', chunkCount, chunkCount);
        const metadata = new FormData();
        metadata.append('hash', hash);
        metadata.append('name', selectedFile.name);
        metadata.append('chunkCount', chunkCount);
        metadata.append('size', selectedFile.size);
        if (expiryDays)    metadata.append('expiryDays', expiryDays);
        if (downloadLimit) metadata.append('downloadLimit', downloadLimit);

        const response = await fetch('/upload', { method: 'POST', body: metadata });
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

function setProgress(msg, completed, total) {
    setStatus(msg);
    const percent = Math.round((completed / total) * 100);
    const bar = htmx.find('#upload-progress');
    if (!bar) return;
    bar.style.width = `${percent}%`;
    bar.textContent = `${percent}%`;
    bar.setAttribute('aria-valuenow', String(percent));
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
