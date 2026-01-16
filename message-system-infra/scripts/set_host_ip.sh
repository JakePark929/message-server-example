#!/bin/bash

# 상위 폴더 .env 경로
ENV_FILE="../.env"

# Windows Git Bash 기준: 첫 번째 유효 IPv4 가져오기
HOST_IP=$(ipconfig | awk '/IPv4/ {gsub(/^[ \t]+|[ \t]+$/,"",$NF); print $NF; exit}')

# .env가 존재하지 않으면 새로 생성
if [ ! -f "$ENV_FILE" ]; then
    echo "HOST_IP=$HOST_IP" > "$ENV_FILE"
else
    # 기존 파일 내용 백업
    TMP_FILE=$(mktemp)

    # HOST_IP 라인 제거 (중복 방지)
    grep -v '^HOST_IP=' "$ENV_FILE" > "$TMP_FILE"

    # 새 HOST_IP를 첫 줄에 추가하고 나머지 내용 붙이기
    {
        echo "HOST_IP=$HOST_IP"
        cat "$TMP_FILE"
    } > "$ENV_FILE"

    # 임시파일 삭제
    rm "$TMP_FILE"
fi

echo ".env 업데이트 완료: HOST_IP=$HOST_IP"