import '@/pages/NotFoundPage.css';

export default function NotFoundPage() {
    return (
        <div className="not-found">
            <div className="not-found__code">404</div>
            <p className="not-found__message">페이지를 찾을 수 없습니다</p>
            <p className="not-found__sub">요청하신 페이지가 존재하지 않습니다</p>
        </div>
    );
}