interface WishToastProps {
    collectionName: string;
    onChangeClick: () => void;
}

export const WishToast = ({ collectionName, onChangeClick }: WishToastProps) => {
    return (
        <div className="wish-toast">
            <img src="/default-thumbnail.png" alt="" className="wish-toast-thumb" />
            <span className="wish-toast-text">
                <b>{collectionName}</b>에 저장됨
            </span>
            <button className="wish-toast-change" onClick={onChangeClick}>
                변경
            </button>
        </div>
    );
};