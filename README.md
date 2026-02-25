# GTransfer

A self-hosted, end-to-end encrypted file transfer service. The server stores only ciphertext and never has access to encryption keys or plaintext.

## How encryption works

### Upload

1. **Key generation** — the browser generates a random 256-bit AES-GCM key using the [Web Crypto API](https://developer.mozilla.org/en-US/docs/Web/API/Web_Crypto_API). The key never leaves the browser.

2. **Encryption** — the file is encrypted with AES-GCM. A random 96-bit (12-byte) IV is generated for each upload. The IV is prepended to the ciphertext to produce the payload:
   ```
   payload = IV (12 bytes) || ciphertext
   ```

3. **File ID** — the server needs a way to identify the file without knowing the key. The client computes `SHA-256(rawKey)` and uses the hex digest as the file ID. This is a one-way operation — the server cannot reverse it to obtain the key.

4. **Upload** — the encrypted payload and file ID are sent to the server. The server stores the ciphertext and records the file ID, name, expiry, and download limit in the database.

5. **Share link** — the raw key is base64url-encoded and placed in the [URL fragment](https://developer.mozilla.org/en-US/docs/Web/API/URL/hash):
   ```
   https://example.com/download#<base64url(rawKey)>
   ```
   Browsers never include the fragment in HTTP requests, so the key is never transmitted to the server.

### Download

1. **Key extraction** — the browser reads the key from the URL fragment and decodes it from base64url.

2. **File lookup** — the client computes `SHA-256(rawKey)` to derive the file ID and fetches the encrypted payload from `/download/<id>/data`.

3. **Decryption** — the IV is read from the first 12 bytes of the payload, and the remainder is decrypted with AES-GCM. AES-GCM is authenticated encryption, so any tampering with the ciphertext causes decryption to fail with an authentication error.

4. **Download** — the plaintext is written to a `Blob` and the browser is prompted to save it.

### Security properties

| Property | Detail |
|---|---|
| Encryption | AES-256-GCM |
| IV | 96-bit random, unique per upload |
| Key derivation | None — key is randomly generated |
| File ID | SHA-256(rawKey) — server cannot reverse to key |
| Key transport | URL fragment — never sent to server |
| Server access | Ciphertext, file metadata, SHA-256 of key only |
