import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { IndividualPerformanceDetailComponent } from './individual-performance-detail.component';

describe('IndividualPerformance Management Detail Component', () => {
  let comp: IndividualPerformanceDetailComponent;
  let fixture: ComponentFixture<IndividualPerformanceDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IndividualPerformanceDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./individual-performance-detail.component').then(m => m.IndividualPerformanceDetailComponent),
              resolve: { individualPerformance: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(IndividualPerformanceDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IndividualPerformanceDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load individualPerformance on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', IndividualPerformanceDetailComponent);

      // THEN
      expect(instance.individualPerformance()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
