import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PtHistory } from './pt-history';

describe('PtHistory', () => {
  let component: PtHistory;
  let fixture: ComponentFixture<PtHistory>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PtHistory],
    }).compileComponents();

    fixture = TestBed.createComponent(PtHistory);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
