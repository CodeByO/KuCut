package com.example.kucut;

// 필요한 URL들을 저장하고 있는 곳입니다.

public class BasicLink {
    private BasicLink() {}
    public class FeedLink{
        public static final String HOME_PAGE = "https://sejong.korea.ac.kr/kr"; // 홈페이지
        public static final String PORTAL_PAGE = "https://portal.korea.ac.kr/"; // 포탈
        public static final String KUSEUM_PAGE = "https://kuseum.korea.ac.kr/"; // 쿠세움
        public static final String BLACKBOARD_PAGE = "https://kulms.korea.ac.kr/"; // 블랙보드
        public static final String ACADEMIC_INFO = "https://libs.korea.ac.kr/"; // 세종 학술 정보원
        public static final String WEB_MAIL = "https://mail.korea.ac.kr/"; // 웹메일
        public static final String ENROLMENT = "https://sugang.korea.ac.kr/"; // 수강신청
        public static final String GRADE_LOOKUP = "https://sugang.korea.ac.kr/grade/"; // 성적 조회
        public static final String ACADEMIC_CALENDAR = "https://sejong.korea.ac.kr/academics/administration/calendar/undergraduate"; // 학사일정
        public static final String CERTIFICATE_APPLICATION = "https://kucert.korea.ac.kr/"; // 증명서 신청
        public static final String PARTICIPATION_YARD = "https://sejong.korea.ac.kr/campuslife/join/request_about_campus"; // 참여마당(학교에 바란다)
        public static final String COUNSELING_CENTER = "https://sejong.korea.ac.kr/mbshome/mbs/counseling/"; // 세종학생 상담센터
        public static final String TEACHING_LEARNING = "https://sejong.korea.ac.kr/mbshome/mbs/kusjctl/index.do"; // 교수학습 지원센터
        public static final String ENTREPRENEURSHIP_EDUCATION = "http://startup.korea.ac.kr/"; // 세종창업 교육센터
        public static final String JOB_CENTER = "https://sejong.korea.ac.kr/mbshome/mbs/cdc/index.do"; // 대학 일자리 센터
        public static final String COMMUNITY_SERVICE = "https://sejong.korea.ac.kr/mbshome/mbs/share/index.do"; // 세종 사회 봉사단
        public static final String DORMITORY_PAGE = "https://dormitel.korea.ac.kr/mbshome/mbs/hoyeon/index.do"; // 호연학사
        public static final String EVERYTIME = "https://everytime.kr/"; // 에브리타임
        public static final String GENERAL_NOTICE = "http://sejong.korea.ac.kr/campuslife/notice/college"; // 학사 일반 공지
        public static final String EVENT_NOTICE = "https://sejong.korea.ac.kr/user/boardList.do?handle=61751&siteId=kr&id=kr_050107000000"; // 교내 행사 공지
        public static final String COVID19_NOTICE = "https://sejong.korea.ac.kr/user/boardList.do?handle=101685&siteId=kr&id=kr_050108020000"; // 코로나 19 공지
        public static final String READING_ROOM = "http://libs.korea.ac.kr:81/"; // 열람실 현황 / 배정
        public static final String SHUTTLE_BUS = "https://sejong.korea.ac.kr/campuslife/facilities/shuttle_bus"; // 셔틀버스 시간표
        public static final String CAMPUS_CAFETERIA = "https://sejong.korea.ac.kr/campuslife/facilities/dining/weeklymenu"; // 교내 식당 식단표
    }
    public class DepartmentLink{
        public static final String DATA_CALCULATE = "http://imath.korea.ac.kr/"; // 데이터계산과학 전공
        public static final String ARTIFICIAL_SECURITY = "https://aisecu.korea.ac.kr/m_main/"; // 인공지능사이버보안학과
        public static final String DISPLAY_SEMICONDUCTOR = "https://sejong.korea.ac.kr/mbshome/mbs/dsphy/index.do"; // 디스플레이 . 반도체물리 학부
        public static final String MATERIAL_CHEMISTRY = "https://sejong.korea.ac.kr/mbshome/mbs/amchem/index.do"; // 신소재화학과
        public static final String COMPUTER_SOFTWARE = "https://software.korea.ac.kr/"; // 컴퓨터융합소프트웨어학과
        public static final String ELECTRONIC_INFORMATION = "https://kueie.korea.ac.kr/"; // 전자및정보공학과
        public static final String LIFE_INFORMATION = "https://biotechnology.korea.ac.kr/"; // 생명정보공학과
        public static final String FOOD_LIFE = "https://kfbt.korea.ac.kr/"; // 식품생명공학과
        public static final String ELECTRONIC_MACHINE = "https://emse.korea.ac.kr/"; // 전자 기계융합공학과
        public static final String ENVIRONMENT_SYSTEM = "https://env.korea.ac.kr/"; // 환경시스템공학과
        public static final String FREE_ENGINEER = "https://sejong.korea.ac.kr/mbshome/mbs/ids/index.do"; // 자유공학부
        public static final String FUTURE_MOBILITY = "https://sejong.korea.ac.kr/mbshome/mbs/am/index.do"; // 미래모빌리티학과
        public static final String INTELLIGENT_SEMICONDUCTOR = "https://sejong.korea.ac.kr/mbshome/mbs/AISEMI/index.do"; // 지능형반도체공학과
        public static final String GOVERNMENT_ADMINISTRATION = "https://spa.korea.ac.kr/"; // 정부행정학부
        public static final String PUBLIC_SOCIAL = "https://sejong.korea.ac.kr/mbshome/mbs/pubs/index.do"; // 공공사회학전공
        public static final String UNIFICATION_DIPLOMACY = "https://sejong.korea.ac.kr/user/indexMain.do?siteId=kuds"; // 통일외교안보전공
        public static final String ECONOMIC_POLICY = "https://economics.korea.ac.kr/"; // 경제정책학전공
        public static final String BIG_DATA = "https://sejong.korea.ac.kr/mbshome/mbs/bigdatascience/index.do"; // 빅데이터사이언스학부
        public static final String INTERNATIONAL_SPORT = "https://sfa.korea.ac.kr/"; // 국제스포츠학부
        public static final String CULTURE_HERITAGE = "https://cuhc.korea.ac.kr/"; // 문화유산융합학부
        public static final String CULTURE_CONTENT = "https://culc.korea.ac.kr/"; // 문화컨텐츠전공
        public static final String KOREA_STUDIES = "https://sejong.korea.ac.kr/mbshome/mbs/koreanstudies/index.do"; // 한국학전공
        public static final String CHINA_STUDIES = "https://gchina.korea.ac.kr/"; // 중국학전공
        public static final String ENGLISH_STUDIES = "https://ell.korea.ac.kr/"; // 영미학전공
        public static final String GERMAN_STUDIES = "https://sejong.korea.ac.kr/mbshome/mbs/german/index.do"; // 독일학전공
        public static final String GLOBAL_MANAGEMENT = "https://ba.korea.ac.kr/"; // 글로벌경영전공
        public static final String DIGITAL_MANAGEMENT = "http://digb.korea.ac.kr/"; // 디지털경영전공
        public static final String BASIC_INTELLIGENCE = "https://sejong.korea.ac.kr/mbshome/mbs/sti/index.do"; // 표준 지식학과
        public static final String MEDICINE_STUDIES = "https://pharm.korea.ac.kr/"; // 약학과
        public static final String SMART_CITY = "https://sejong.korea.ac.kr/mbshome/mbs/smartcity/index.do"; //스마트도시학부



    }
}
