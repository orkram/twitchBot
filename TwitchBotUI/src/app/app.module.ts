import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppComponent} from './app.component';
import {TwitchChatComponent} from './twitch-chat/twitch-chat.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TwitchAppComponent} from './twitch-app/twitch-app.component';

@NgModule({
  declarations: [
    AppComponent,
    TwitchChatComponent,
    TwitchAppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
