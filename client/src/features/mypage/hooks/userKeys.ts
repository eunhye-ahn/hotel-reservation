export const userKeys = {
    all: ["user"] as const,
    myInfo: () => [...userKeys.all, "myInfo"] as const
}