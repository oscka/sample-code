# sample-code

https://osc-korea.atlassian.net/wiki/home 에서 제공하는 예제 code

## 하위 폴더만 clone 하는 방법
sample-code 하위에 여러 프로젝트가 있는데, 그중 특정 폴더만 clone 하는 방법입니다.

```shell
# 1. git clone 할 폴더로 이동후 아래 command 실행
git init

# 2. git 저장소 연결
# git remote add origin {저장소주소}
git remote add origin https://github.com/oscka/sample-code.git

# 3. git sparse checkout 활성화
git config core.sparsecheckout true

# 4. clone 하기 위한 폴더 경로 설정
# echo {폴더경로}/* >> .git/info/sparse-checkout
echo spring-cache/* >> .git/info/sparse-checkout # spring-cache 프로젝트만 clone 하고 싶을 경우

# 5. 해당 폴더 다운
# git pull origin {브랜치명}
git pull origin main

# 6. 확인
# sample-code/spring-cache 가 아닌, spring-cache 폴더만 존재함.
user@DESKTOP /c/_DEV/_test (master|SPARSE) ll
drwxr-xr-x 1 user 197121 0 Apr 27 17:22 spring-cache/
```

## 현재까지 등록된 내용

### Spring
1. [Spring Cache](https://github.com/oscka/sample-code/tree/main/spring-cache)
2. [Spring Session](https://github.com/oscka/sample-code/tree/main/spring-session)
3. [Spring Batch](https://github.com/oscka/sample-code/tree/main/spring-batch)

