# Release notes v0.2.9-2
## Changes compared to previous eyevinn release (v0.2.9-1)
- AWS crt-based s3 client is now used. This makes multipart uploads more efficient and reliable.

## Changes compared to latest upstream release (v0.2.9)
- Add documentation for eyevinn encore fork
- Fix regex for parsing progress now works for ffmpeg 8
- Add support for encoding audio separately without segmenting when using segmented encoding
- Audio mix presets can have separate location
- Support for specifying custom filters for split,scale,crop,pad
- Sanitize ffprobe input parameters
- Support for input with mix of mono and multitrack audiostreams
- Support s3 urls for input and output

# Release Notes v0.2.9-1
## Changes compared to previous eyevinn release (v0.2.8-7)
- Add documentation for eyevinn encore fork
- Dependecy updates

## Changes compared to latest upstream release (v0.2.9)
- Add documentation for eyevinn encore fork
- Fix regex for parsing progress now works for ffmpeg 8
- Add support for encoding audio separately without segmenting when using segmented encoding
- Audio mix presets can have separate location
- Support for specifying custom filters for split,scale,crop,pad
- Sanitize ffprobe input parameters
- Support for input with mix of mono and multitrack audiostreams
- Support s3 urls for input and output
