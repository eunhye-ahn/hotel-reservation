export interface LoginRequest {
    email: string,
    password: string | null
}

export interface TokenResponse {
    accessToken: string,
    refreshToken: string
}

export interface AccessTokenResponse {
    accessToken: string
}

export interface SignUpRequest {
    name: string,
    email: string,
    password: string | null,
    phone: string
}