import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { RetrospectiveDetailComponent } from './retrospective-detail.component';

describe('Retrospective Management Detail Component', () => {
  let comp: RetrospectiveDetailComponent;
  let fixture: ComponentFixture<RetrospectiveDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RetrospectiveDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./retrospective-detail.component').then(m => m.RetrospectiveDetailComponent),
              resolve: { retrospective: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(RetrospectiveDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RetrospectiveDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load retrospective on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', RetrospectiveDetailComponent);

      // THEN
      expect(instance.retrospective()).toEqual(expect.objectContaining({ id: 123 }));
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
