import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TwitchAppComponent } from './twitch-app.component';

describe('TwitchAppComponent', () => {
  let component: TwitchAppComponent;
  let fixture: ComponentFixture<TwitchAppComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TwitchAppComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TwitchAppComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
