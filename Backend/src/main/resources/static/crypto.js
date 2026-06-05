var DEFAULT_CHUNK_SIZE = 4 * 1024 * 1024;

async function encryptFile(file, chunkSize = DEFAULT_CHUNK_SIZE) {
    const key = await crypto.subtle.generateKey(
        { name: 'AES-GCM', length: 256 },
        true,
        ['encrypt', 'decrypt']
    );

    const rawKey = await crypto.subtle.exportKey('raw', key);
    const hash = await hashKey(rawKey);
    const base64urlKey = encodeKey(rawKey);
    const chunkCount = Math.max(1, Math.ceil(file.size / chunkSize));

    return {
        hash,
        base64urlKey,
        chunkCount,
        chunks: encryptedChunks(file, key, chunkCount, chunkSize)
    };
}

async function* encryptedChunks(file, key, chunkCount, chunkSize) {
    for (let index = 0; index < chunkCount; index++) {
        const start = index * chunkSize;
        const end = Math.min(start + chunkSize, file.size);
        const plaintext = await file.slice(start, end).arrayBuffer();
        const iv = crypto.getRandomValues(new Uint8Array(12));
        const ciphertext = await crypto.subtle.encrypt({ name: 'AES-GCM', iv }, key, plaintext);

        const payload = new Uint8Array(12 + ciphertext.byteLength);
        payload.set(iv, 0);
        payload.set(new Uint8Array(ciphertext), 12);

        yield { index, payload };
    }
}

async function hashKey(rawKey) {
    return Array.from(new Uint8Array(await crypto.subtle.digest('SHA-256', rawKey)))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');
}

async function decryptChunk(payload, key) {
    const bytes = new Uint8Array(payload);
    const iv = bytes.slice(0, 12);
    const ciphertext = bytes.slice(12);
    return crypto.subtle.decrypt({ name: 'AES-GCM', iv }, key, ciphertext);
}

function encodeKey(rawKey) {
    return btoa(String.fromCharCode(...new Uint8Array(rawKey)))
        .replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}

function decodeKey(base64url) {
    const base64 = base64url.replace(/-/g, '+').replace(/_/g, '/');
    return Uint8Array.from(atob(base64), c => c.charCodeAt(0));
}
