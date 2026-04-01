import { ref } from 'vue'

const toasts = ref([])

let id = 0

export function useToast() {

    const showToast = (message, type = 'success', duration = 3000) => {
        const toast = {
            id: id++,
            message,
            type
        }

        toasts.value.push(toast)

        setTimeout(() => {
            removeToast(toast.id)
        }, duration)
    }

    const removeToast = (toastId) => {
        toasts.value = toasts.value.filter(t => t.id !== toastId)
    }

    return {
        toasts,
        showToast
    }
}