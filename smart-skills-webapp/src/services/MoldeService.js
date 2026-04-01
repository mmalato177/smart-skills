
export async function getMoldes() {
  const response = await fetch('/api/moldes', {
    credentials: 'include'
  })

  if (!response.ok) {
    throw new Error(`Erro ao buscar moldes: ${response.status}`)
  }

  return await response.json()
}

export async function uploadMoldes(filesWithCompany) {
    const formData = new FormData()

    filesWithCompany.forEach(item => {
        formData.append('files', item.file)
        formData.append('companies', item.company)
    })

    const response = await fetch('/api/moldes/upload', {
        method: 'POST',
        credentials: 'include',
        body: formData
    })

    if (!response.ok) {
        const errorText = await response.text()
        console.error('BACKEND RESPONSE:', errorText)
        throw new Error(errorText || 'Failed to upload files')
    }

    return await response.json()
}

export async function getMoldeByWKZ(wkz) {
    const response = await fetch(`/api/moldes/${wkz}`, {
        credentials: 'include'
    })
    if (!response.ok) {
        throw new Error(`Erro ao buscar molde: ${response.status}`)
    }

    return await response.json()
}


export async function getCompany() {
  const response = await fetch('/api/enums/company', {
    credentials: 'include'
  })

  if (!response.ok) {
    throw new Error(`Erro ao buscar companies: ${response.status}`)
  }

  return await response.json()
}

export async function getStatus() {
  const response = await fetch('/api/enums/status', {
    credentials: 'include'
  })

  if (!response.ok) {
    throw new Error(`Erro ao buscar status: ${response.status}`)
  }

  return await response.json()
}