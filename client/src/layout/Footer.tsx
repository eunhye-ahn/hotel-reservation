export const Footer = () => {
    return (
        <footer className="border-t border-gray-200 mt-16 bg-white">
            <div className="max-w-6xl mx-auto px-6 py-10">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-10">
                    <div>
                        <h3 className="text-sm font-bold text-gray-900 mb-3">호텔예약</h3>
                        <p className="text-xs text-gray-500 leading-relaxed">
                            전국 호텔을 한 곳에서 편리하게 예약하는<br />
                            포트폴리오 프로젝트입니다.
                        </p>
                    </div>

                    <div>
                        <h4 className="text-xs font-semibold text-gray-700 mb-3">Contact</h4>
                        <ul className="space-y-2 text-xs text-gray-500">
                            <li>
                                <a href="https://github.com/여기에깃허브아이디" target="_blank" rel="noopener noreferrer" className="hover:text-gray-900">
                                    GitHub
                                </a>
                            </li>
                            <li>
                                <a href="https://여기에노션페이지주소" target="_blank" rel="noopener noreferrer" className="hover:text-gray-900">
                                    Notion
                                </a>
                            </li>
                            <li>
                                <a href="mailto:여기에이메일" className="hover:text-gray-900">
                                    Email
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>

                <div className="border-t border-gray-100 mt-8 pt-6 flex items-center justify-between flex-wrap gap-2">
                    <p className="text-xs text-gray-400">
                        &copy; {new Date().getFullYear()} 호텔예약/결제/정산 전자상거래 플랫폼
                    </p>
                    <p className="text-xs text-gray-400">
                        Spring Boot · React · PostgreSQL · Redis
                    </p>
                </div>
            </div>
        </footer>
    )
}