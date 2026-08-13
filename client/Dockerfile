#nginx 이미지 사용
FROM nginx:latest

#nginx 기본 설정 파일 삭제 => custom default.conf로 교체
RUN rm /etc/nginx/conf.d/default.conf

#빌드한 정적파일을 nginx의 서빙 표준경로에 복사
COPY ./dist /usr/share/nginx/html

#커스텀 nginx 설정파일을 nginx가 읽어들이는 경로로에복사
COPY ./default.conf /etc/nginx/conf.d/default.conf

#80번 포트로 노출선언
EXPOSE 80

#nginx 실행
CMD ["nginx", "-g", "daemon off;"]