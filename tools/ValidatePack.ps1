$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path $PSScriptRoot -Parent
$root = Join-Path $projectRoot 'resourcepack'
Add-Type -AssemblyName System.Drawing
Add-Type -AssemblyName System.IO.Compression.FileSystem
foreach ($file in (Get-ChildItem $root -Recurse -File | Where-Object { $_.Extension -eq '.json' -or $_.Extension -eq '.mcmeta' })) { $null = [IO.File]::ReadAllText($file.FullName) | ConvertFrom-Json }
$font = [IO.File]::ReadAllText((Join-Path $root 'assets/tilkitags/font/badges.json')) | ConvertFrom-Json
$bitmaps = @($font.providers | Where-Object { $_.type -eq 'bitmap' })
if ($bitmaps.Count -ne 18) { throw 'Expected 17 rank badges and the compact Tilki badge' }
$spaces = @($font.providers | Where-Object { $_.type -eq 'space' })
if ($spaces.Count -ne 1 -or @($spaces[0].advances.PSObject.Properties).Count -ne 8) { throw 'TAB padding spaces missing' }
$glyphs = @{}
foreach ($provider in $bitmaps) {
    $texture = Join-Path $root ('assets/tilkitags/textures/' + $provider.file.Substring('tilkitags:'.Length))
    $bitmap = [Drawing.Bitmap]::new($texture)
    try {
        if ($provider.chars.Count -ne 1 -or $provider.chars[0].Length -ne 1) { throw 'Expected one glyph per texture' }
        if ($bitmap.Width -gt 256 -or $bitmap.Height -gt 256) { throw 'Bitmap exceeds client font atlas' }
        # Rank badges fill one 9 px chat line; the Tilki badge is 14 px on purpose and rises above it.
        $expected=if($provider.file -eq 'tilkitags:font/tilki.png'){14}else{9}
        if ($provider.height -ne $expected -or $provider.ascent -ne $expected-1) { throw 'Badge height or baseline invalid' }
        $character = [int][char]$provider.chars[0]
        if ($glyphs.ContainsKey($character)) { throw 'Duplicate glyph' }
        $glyphs[$character]=$true
        if ($bitmap.GetPixel(0,0).A -ne 0) { throw 'Missing transparent icon margin' }
    } finally { $bitmap.Dispose() }
}
foreach ($codepoint in 0xeb00..0xeb11) { if (!$glyphs.ContainsKey($codepoint)) { throw 'Missing badge glyph' } }
foreach ($name in @('default','uniform')) {
    $definition = [IO.File]::ReadAllText((Join-Path $root ('assets/minecraft/font/'+$name+'.json'))) | ConvertFrom-Json
    if ($definition.providers[0].id -ne 'tilkitags:badges') { throw 'Font reference missing' }
}
$zipPath = Join-Path $projectRoot 'dist/TilkiTags-ResourcePack.zip'
$zip = [IO.Compression.ZipFile]::OpenRead($zipPath)
try {
    if (!$zip.GetEntry('pack.mcmeta') -or !$zip.GetEntry('assets/tilkitags/font/badges.json')) { throw 'Invalid ZIP root' }
    if ($zip.Entries.Count -ne 22) { throw 'Unexpected ZIP entry count' }
} finally { $zip.Dispose() }
$hash = (Get-FileHash $zipPath -Algorithm SHA1).Hash.ToLowerInvariant()
if ($hash -ne ([IO.File]::ReadAllText((Join-Path $projectRoot 'dist/resourcepack-sha1.txt'))).Trim()) { throw 'SHA1 mismatch' }
if (!([IO.File]::ReadAllText((Join-Path $projectRoot 'src/main/resources/config.yml'))).Contains($hash)) { throw 'Default pack hash mismatch' }
Write-Output 'VALID: 17 reference badge textures, one-line chat height, taller chat Tilki, compact TAB Tilki, padding spaces, glyph baselines, fonts, ZIP and SHA1.'
