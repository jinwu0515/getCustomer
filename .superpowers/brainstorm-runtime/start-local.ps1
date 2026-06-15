param(
  [Parameter(Mandatory = $true)]
  [string]$SessionDir
)

$ErrorActionPreference = "Stop"

$env:BRAINSTORM_DIR = $SessionDir
$env:BRAINSTORM_HOST = "0.0.0.0"
$env:BRAINSTORM_URL_HOST = "localhost"

$scriptPath = Join-Path $PSScriptRoot "scripts/server.cjs"
node $scriptPath
