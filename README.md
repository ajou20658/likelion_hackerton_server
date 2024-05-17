<img src="https://capsule-render.vercel.app/api?type=waving&color=7DDA58&height=100&section=header&text=&fontSize=0" width="100%" alt="header"/>

## 기술스택

---

![Spring-Boot](https://img.shields.io/badge/spring%20boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)

![MongoDB](https://img.shields.io/badge/mongodb-47A248.svg?style=for-the-badge&logo=mongodb&logoColor=white)
![Mysql](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)

![Nginx](https://img.shields.io/badge/nginx-009639.svg?style=for-the-badge&logo=nginx&logoColor=white)

---

# API 명세서
### 수동으로 키워드 추출
* URL: `/api/keyword`
* Method: `GET`
* RequestBody:
```
{
  "response" : [
    "keyword1",
    "keyword2",
  ]
}
```
### 특정 분야 뉴스 크롤링 + 요약(by clova summary api)
* URL: `/api/save-summary?sid1={네이버 기사의 subject Id}`
* Method: `GET`

### 특정 뉴스 요약(by clova summary api)
* URL: `/api/summary?url={news url}`
* Method: `GET`
* RequestBody:
```
{
    "summary": "간편송금 이용금액이 하루 평균 2000억원을 넘어섰습니다.\n한은은 카카오페이, 토스 등 간편송금 서비스를 제공하는 업체 간 경쟁이 심화되면서 이용규모가 크게 확대됐다고 분석했습니다.\n국회 정무위원회 소속 바른미래당 유의동 의원에 따르면 카카오페이, 토스 등 선불전자지급서비스 제공업체는 지난해 마케팅 비용으로 1000억원 이상을 지출했습니다."
}
```
### 키워드 검색
* URL: `/api/search?keyword={keyword}`
* Method: `GET`
* RequestHeader: `"Authorization":"Bearer {refreshToken}"`
* ResponseBody:
```
{
  "response": [
    {
      "title":"제목",
      "desc":"설명",
      "imgUrl":"기사이미지",
      "summary":"요약내용",
      "press":"출판사",
      "origin":"원문 url"
    },
  ] 
}
```

<img src="https://capsule-render.vercel.app/api?type=waving&color=7DDA58&height=150&section=footer" width="100%" alt="footer"/>
