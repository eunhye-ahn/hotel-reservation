//package com.hotel.hotel_server.repository;
//
//import com.hotel.hotel_server.domain.HotelDocument;
//import com.hotel.hotel_server.dto.HotelSearchRequest;
//import com.hotel.hotel_server.dto.HotelSearchResult;
//import com.hotel.hotel_server.exception.CustomException;
//import com.hotel.hotel_server.exception.ErrorCode;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.elasticsearch.client.elc.NativeQuery;
//import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
//import org.springframework.data.elasticsearch.core.SearchHit;
//import org.springframework.data.elasticsearch.core.SearchHits;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//
//import java.util.Base64;
//import java.util.List;
//import java.util.Objects;
//
///**
// * reqository에서 query 빌드
// *      => service는 비즈니스 로직만 담당하도록 책임 분리
// *  es(검색) => id 반환 => querydsl(메인) -가격/인원/예약 가능여부
// */
//@Repository
//@RequiredArgsConstructor
//public class HotelSearchQueryRepository {
//    private final ElasticsearchOperations operations;
//
//    public HotelSearchResult search(HotelSearchRequest request){
//        NativeQuery query = NativeQuery.builder()
//                .withQuery(q -> q
//                        .bool(b-> {  //bool쿼리 -조건묶음
//                            //검색어 -> hotelName, address, 숙박유형 => text
//                            if (StringUtils.hasText(request.getQ())) {
//                                //점수반영 must type -> multimatch쿼리로 작성
//                                b.must(m -> m
//                                        .multiMatch(mm -> mm
//                                                .query(request.getQ())
//                                                .fields("hotelName^3", "address^2")));
//                            }
//                            //점수무관 filter type -> term쿼리로 작성
//                            //시도필터링
//                            if (StringUtils.hasText(request.getLDongRegnCd())) {
//                                b.filter(f -> f
//                                        .term(t -> t
//                                                .field("lDongRegnCd")
//                                                .value(request.getLDongRegnCd())));
//                            }
//                            //시군구필터링
//                            if (StringUtils.hasText(request.getLDongSignguCd())) {
//                                b.filter(f -> f
//                                        .term(t -> t
//                                                .field("lDongSignguCd")
//                                                .value(request.getLDongSignguCd())));
//                            }
//                            //숙박유형필터링
//                            if (StringUtils.hasText(request.getLclsSystm2())) {
//                                b.filter(f -> f
//                                        .term(t -> t
//                                                .field("lclsSystm2")
//                                                .value(request.getLclsSystm2())));
//                            }
//                            //검색어없으면 전체조회
//                            if(!StringUtils.hasText(request.getQ())){
//                                b.must(m->m.matchAll(ma->ma));
//                            }
//                            return b;
//                        }))
//                //커서 정렬보조
//                .withSort(Sort.by(
//                        Sort.Order.desc("_score"),
//                        Sort.Order.asc("hotelId")
//                ))
//                //커서여부
//                .withSearchAfter(
//                        StringUtils.hasText(request.getCursor())
//                        ? decodedCursor(request.getCursor())
//                                : null)
//                //커서의 시작위치 : withSerachAfter이 처리하므로 항상 0으록 고정
//                //+1을 하는 이유 -hasNext판별을 위해
//                .withPageable(PageRequest.of(0,request.getSize()+1))
//                .build();
//
//        //쿼리결과(+1한 결과 반환)
//        SearchHits<HotelDocument> hits = operations.search(query, HotelDocument.class);
//        List<SearchHit<HotelDocument>> hitList = hits.getSearchHits();
//
//        //hasNext 판별
//        boolean hasNext = hitList.size() > request.getSize();
//        //+1 제거
//        if(hasNext){
//            hitList = hitList.subList(0,request.getSize());
//        }
//
//        //next cursor 생성
//        String nextCursor = null;
//        if(hasNext){
//            nextCursor = encodedCursor(hitList.get(hitList.size()-1));
//        }
//
//        //hotelId목록 추출
//        List<Long> hotelIds = hitList.stream()
//                .map(hit -> hit.getContent().getHotelId())
//                .toList();
//
//        return HotelSearchResult.builder()
//                .hotelIds(hotelIds)
//                .nextCursor(nextCursor)
//                .hasNext(hasNext)
//                .build();
//    }
//
//    /**
//     * cursor decoding
//     *
//     * [WHY] 시작값 설정
//     *          searchAfter(1.5,47) score가 1.5이고 hotelId 42이후 데이터 가져오기
//     *
//     * 클라이언트가 보낸 문자열 => cursor="문자열"
//     * 디코딩해서 json으로 변환
//     * json 파싱해서 배열로 저장 => searchAfter에 넣기 위해 -es가 인식가능한 형태
//     *
//     * @param cursor
//     * @return json cursor 배열화
//     */
//    private List<Object> decodedCursor(String cursor){
//        try{
//            String decoded = new String(Base64.getDecoder().decode(cursor));
//            //문자열 배열로 변환
//            String[] parts = decoded.split(",");
//            return List.of(
//                    Double.parseDouble(parts[0]),   //score
//                    Long.parseLong(parts[1])                        //마지막 hotelId string
//            );
//
//        }catch (Exception e){
//            throw new CustomException(ErrorCode.INVALID_CURSOR);
//        }
//    }
//
//    //인코딩 커서
//    private String encodedCursor(SearchHit<HotelDocument> lastHit){
//        String raw = lastHit.getScore() + "," + lastHit.getContent().getHotelId();
//        return Base64.getEncoder().encodeToString(raw.getBytes());
//    }
//}
