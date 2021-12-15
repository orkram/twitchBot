import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BettingComponentComponent } from './betting-component.component';

describe('BettingComponentComponent', () => {
  let component: BettingComponentComponent;
  let fixture: ComponentFixture<BettingComponentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BettingComponentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BettingComponentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
