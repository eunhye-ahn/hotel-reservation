export const wishCollectionKeys = {
    all: ["wishCollections"] as const,
    lists: () => [...wishCollectionKeys.all, "list"] as const,
    list: () => [...wishCollectionKeys.lists()] as const,
    details: () => [...wishCollectionKeys.all, "detail"] as const,
    detail: (collectionId: number) => [...wishCollectionKeys.details(), collectionId] as const
}