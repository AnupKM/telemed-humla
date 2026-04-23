import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PtRecord } from './pt-record';

describe('PtRecord', () => {
  let component: PtRecord;
  let fixture: ComponentFixture<PtRecord>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PtRecord],
    }).compileComponents();

    fixture = TestBed.createComponent(PtRecord);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
