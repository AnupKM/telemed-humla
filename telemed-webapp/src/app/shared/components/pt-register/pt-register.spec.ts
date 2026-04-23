import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PtRegisterPage } from './pt-register';

describe('PtRegisterPage', () => {
  let component: PtRegisterPage;
  let fixture: ComponentFixture<PtRegisterPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PtRegisterPage],
    }).compileComponents();

    fixture = TestBed.createComponent(PtRegisterPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
