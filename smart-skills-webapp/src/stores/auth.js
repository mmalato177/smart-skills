// src/stores/auth.js
import { defineStore } from 'pinia'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        initialized: false
    }),

    getters: {
        isAuthenticated: (state) => !!state.user
    },

    actions: {
        async login(username, password) {
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({ username, password })
            })

            if (!res.ok) {
                throw new Error('Login inválido')
            }

            await this.fetchMe()
        },

        async fetchMe() {
            try {
                const res = await fetch('/api/auth/me', {
                    credentials: 'include'
                })

                if (!res.ok) {
                    this.user = null
                    this.initialized = true
                    return
                }

                this.user = await res.json()
            } catch {
                this.user = null
            } finally {
                this.initialized = true
            }
        },

        async logout() {
            await fetch('/api/auth/logout', {
                method: 'POST',
                credentials: 'include'
            })

            this.user = null
            this.initialized = true
        },

        async uploadProfileImage(file) {
            try {
                const formData = new FormData()
                formData.append('image', file)

                const res = await fetch('/api/auth/profile-image', {
                    method: 'POST',
                    credentials: 'include',
                    body: formData
                })

                if (!res.ok) {
                    const error = await res.json().catch(() => null)

                    let errorMessage = error?.message || 'Erro ao fazer upload da imagem'

                    if (res.status === 413) {
                        errorMessage = 'A imagem é demasiado grande.'
                    }

                    throw new Error(errorMessage)
                }

                return await res.json()
            } catch (error) {
                console.error('Erro upload imagem:', error)
                throw error
            }
        },

        async changePassword(currentPassword, newPassword) {
            const res = await fetch('/api/auth/change-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({ currentPassword, newPassword })
            })

            if (!res.ok) {
                const error = await res.json().catch(() => null)
                const errorMessage = error?.message || 'Erro ao alterar senha'
                throw new Error(errorMessage)
            }

            return await res.json()
        }
    }
})