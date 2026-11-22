$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

$line = Get-Content -LiteralPath '.env' | Where-Object { $_ -match '^CEREBRAS_API_KEY=' } | Select-Object -First 1
if (-not $line) {
    throw 'CEREBRAS_API_KEY não encontrada em backend-spring/.env.'
}

$env:CEREBRAS_API_KEY = ($line -split '=', 2)[1].Trim()
if (-not $env:CEREBRAS_API_KEY) {
    throw 'CEREBRAS_API_KEY está vazia em backend-spring/.env.'
}

if (-not $env:ALLOWED_ORIGINS) {
    $env:ALLOWED_ORIGINS = 'http://127.0.0.1:5173,http://localhost:5173'
}

New-Item -ItemType Directory -Path '.local-data' -Force | Out-Null
& .\mvnw.cmd '-Dspring-boot.run.profiles=local' spring-boot:run
