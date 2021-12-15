import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WhitelistComponentComponent } from './whitelist-component.component';

describe('WhitelistComponentComponent', () => {
  let component: WhitelistComponentComponent;
  let fixture: ComponentFixture<WhitelistComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WhitelistComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WhitelistComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
