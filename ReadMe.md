# WebSocket 기반 메세지 서버

웹소켓 기반 멀티모듈 메세지 시스템 프로젝트 입니다.  
CLI 클라이언트와 웹소켓 서버로 구성되어 있습니다.

## 🧑‍💻 Tech Stack
|   구분   | 기술명                                                                |
|:------:|:-------------------------------------------------------------------|
| Common | - Java 17.0.13 (Eclipse Temurin) <br/> - Spock 4 (테스트 프레임워크)       |
| Client | - JLine 3.28.0 (CLI 클라이언트) <br/> - Tyrus 2.2.0 (WebSocket Engine)  |
| Server | - SpringBoot 3.4.11(LTS) <br/> - Spring Security <br/> - WebSocket |
| Infra  | - MySQL 8.0.40 (RDB) <br/> - Redis 7.4.1                           |

> Tyrus = Jakarta WebSocket(JSR 356)의 표준 구현체

## 🗂️ 프로젝트 구조

```
message-system/
├── message-system-client/ # 클라이언트 모듈
│ ├── data/
│ │ └── history.txt # 클라이언트 메시지 히스토리 저장
│ └── src/
│ ├── main/java/com/jake/messagesystem/
│ │ ├── dto/ # 데이터 전송 객체 (DTO)
│ │ ├── handler/ # 이벤트/메시지 핸들러
│ │ ├── service/ # 서비스 계층
│ │ ├── util/ # 유틸리티 클래스
│ │ └── MessageClient.java # 클라이언트 실행 진입점
│
├── message-system-server/ # 서버 모듈
│ └── src/
│ ├── main/java/com/jake/messagesystem/
│ │ ├── config/ # 서버 설정
│ │ ├── dto/ # 데이터 전송 객체 (DTO)
│ │ ├── handler/ # WebSocket 등 핸들러
│ │ ├── session/ # 세션 관리 로직
│ │ └── MessageSystemApplication.java # 서버 실행 진입점
│ └── resources/ # 리소스 파일
│ └── test/ # 테스트 코드
```

## 📖 메세징 서버 관련 지식

> 브로드캐스트 : 농사할 때 씨앗을 밭에 흩뿌리는 모습   
> 농사 -> 라디오, 방송 -> IT로 용어가 전파됨

|    구분     |                       네트워크 브로드캐스트                        |                        웹소켓 서버 브로드캐스트 (멀티플 유니캐스트)                         |
|:---------:|:--------------------------------------------------------:|:------------------------------------------------------------------------:|
|   동작 계층   | L2(Data Link) / L3(Network) 스위치 <br/> 라우터 등 하위 계층 패킷 전송  | L4(Transport) / L5(Session) / L7(Application) Layer <br/> TCP 위 웹소켓 프로토콜 |
| 라우터 통과 여부 |               라우터는 기본적으로 브로드캐스트 원격지 전파 차단                |                                원격지로 전파 가능                                |
|  패킷 목적지   | 브로드캐스트 주소 <br/> (ex: 255.255.255.255 or 192.168.1.255 등) |                                각 클라이언트 소켓                                |
|  메세지 전달   |       논리적으로 한 번의 브로드 캐스트 <br/> 네트워크 내 모든 호스트 패킷 전파       |                    각 클라이언트 소켓으로 유니캐스트 <br/> 개별 전송 반복                     |
|   사용 예    |                       ARP, DHCP 등                        |                           그룹 채팅 메세지, 알림 시스템 등                            |

> - ARP(Address Resolution Protocol) : 특정 Host IP를 알지만 MAC Address 를 모를때 ARP 패킷 전파,  
>   IP 로 MAC Address 를 알 수 있음 (+RARP : MAC Address 를 알 때, RARP로 IP 주소 할당, 지금은 DHCP가 대체)  
> - DHCP(Dynamic Host Configuration Protocol) : 네트워크 내 DHCP 서버가 IP 주소와 관련 네트워크 설정을 자동으로 클라이언트에 할당해 주는 프로토콜 (공유기/라우터 등에 내장)   
