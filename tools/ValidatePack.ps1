$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$root = Join-Path $projectRoot 'resourcepack'
Add-Type -AssemblyName System.Drawing
Add-Type -AssemblyName System.IO.Compression.FileSystem
foreach ($file in (Get-ChildItem $root -Recurse -File | Where-Object { $_.Extension -eq '.json' -or $_.Extension -eq '.mcmeta' })) { $null = [IO.File]::ReadAllText($file.FullName) | ConvertFrom-Json }
$font = [IO.File]::ReadAllText((Join-Path $root 'assets/tilkitags/font/badges.json')) | ConvertFrom-Json
if ($font.providers.Count -ne 17) { throw 'Expected 17 rank badges' }
$glyphs = @{}
foreach ($provider in $font.providers) {
    $texture = Join-Path $root ('assets/tilkitags/textures/' + $provider.file.Substring('tilkitags:'.Length))
    $bitmap = [Drawing.Bitmap]::new($texture)
    try {
        if ($provider.chars.Count -ne 1 -or $provider.chars[0].Length -ne 1) { throw 'Expected one glyph per texture' }
        if ($bitmap.Width -gt 256 -or $bitmap.Height -gt 256) { throw 'Bitmap exceeds client font atlas' }
        $isTilki = $provider.file -eq 'tilkitags:font/tilki.png'
        if ((!$isTilki -and ($provider.height -ne 9 -or $provider.ascent -ne 8)) -or ($isTilki -and ($provider.height -ne 11 -or $provider.ascent -ne 10))) { throw 'Badge does not fit the text baseline' }
        $character = [int][char]$provider.chars[0]
        if ($glyphs.ContainsKey($character)) { throw 'Duplicate glyph' }
        $glyphs[$character]=$true
        if ($bitmap.GetPixel(0,0).A -ne 0) { throw 'Missing transparent icon margin' }
    } finally { $bitmap.Dispose() }
}
foreach ($codepoint in 0xeb00..0xeb10) { if (!$glyphs.ContainsKey($codepoint)) { throw 'Missing badge glyph' } }
foreach ($name in @('default','uniform')) {
    $definition = [IO.File]::ReadAllText((Join-Path $root ('assets/minecraft/font/'+$name+'.json'))) | ConvertFrom-Json
    if ($definition.providers[0].id -ne 'tilkitags:badges') { throw 'Font reference missing' }
}
$zipPath = Join-Path $projectRoot 'dist/TilkiTags-ResourcePack.zip'
$zip = [IO.Compression.ZipFile]::OpenRead($zipPath)
try {
    if (!$zip.GetEntry('pack.mcmeta') -or !$zip.GetEntry('assets/tilkitags/font/badges.json')) { throw 'Invalid ZIP root' }
    if ($zip.Entries.Count -ne 21) { throw 'Unexpected ZIP entry count' }
} finally { $zip.Dispose() }
$hash = (Get-FileHash $zipPath -Algorithm SHA1).Hash.ToLowerInvariant()
if ($hash -ne ([IO.File]::ReadAllText((Join-Path $projectRoot 'dist/resourcepack-sha1.txt'))).Trim()) { throw 'SHA1 mismatch' }
if (!([IO.File]::ReadAllText((Join-Path $projectRoot 'src/main/resources/config.yml'))).Contains($hash)) { throw 'Default pack hash mismatch' }
Write-Output 'VALID: 17 badge textures, enlarged Tilki badge, glyph baselines, fonts, ZIP and SHA1.'
