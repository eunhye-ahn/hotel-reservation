
export const getToday = () => {
    return new Date().toLocaleDateString('en-CA')
}

export const getTomorrow = () => {
    return new Date(Date.now() + 86400000).toLocaleDateString('en-CA')
}

export const getDaysAgo = (days: number): string => {
    return new Date(Date.now() + days * 24 * 60 * 60 * 1000).toLocaleDateString('en-CA')
}

export const addDays = (dateStr: string, days: number): string => {
    const date = new Date(dateStr)
    date.setDate(date.getDate() + days)
    return date.toLocaleDateString('en-CA')
}