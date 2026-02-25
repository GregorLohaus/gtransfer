async function encryptFile(arrayBuffer) {
    const key = await crypto.subtle.generateKey(
        { name: 'AES-GCM', length: 256 },
        true,
        ['encrypt', 'decrypt']
    );

    const iv = crypto.getRandomValues(new Uint8Array(12));

    const ciphertext = await crypto.subtle.encrypt({ name: 'AES-GCM', iv }, key, arrayBuffer);

    // Payload: 12-byte IV prepended to ciphertext
    const payload = new Uint8Array(12 + ciphertext.byteLength);
    payload.set(iv, 0);
    payload.set(new Uint8Array(ciphertext), 12);

    // SHA-256(rawKey) → file identifier sent to server; server never sees the key itself
    const rawKey = await crypto.subtle.exportKey('raw', key);
    const hash = await hashKey(rawKey);

    const base64urlKey = encodeKey(rawKey);

    return { payload, hash, base64urlKey };
}

async function hashKey(rawKey) {
    return Array.from(new Uint8Array(await crypto.subtle.digest('SHA-256', rawKey)))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');
}

async function decryptFile(payload, key) {
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
