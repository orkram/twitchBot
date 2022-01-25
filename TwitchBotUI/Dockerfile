FROM node:12.18.2 as build

WORKDIR /usr/local/app

COPY ./ /usr/local/app/

RUN npm install

RUN npm run build

FROM nginx:latest

COPY --from=build //usr/local/app/dist/TwitchBotUI/ /usr/share/nginx/html

EXPOSE 80
