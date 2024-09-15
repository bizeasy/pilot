import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { RetrospectiveService } from '../service/retrospective.service';
import { IRetrospective } from '../retrospective.model';
import { RetrospectiveFormService } from './retrospective-form.service';

import { RetrospectiveUpdateComponent } from './retrospective-update.component';

describe('Retrospective Management Update Component', () => {
  let comp: RetrospectiveUpdateComponent;
  let fixture: ComponentFixture<RetrospectiveUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let retrospectiveFormService: RetrospectiveFormService;
  let retrospectiveService: RetrospectiveService;
  let sprintService: SprintService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RetrospectiveUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(RetrospectiveUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RetrospectiveUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    retrospectiveFormService = TestBed.inject(RetrospectiveFormService);
    retrospectiveService = TestBed.inject(RetrospectiveService);
    sprintService = TestBed.inject(SprintService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Sprint query and add missing value', () => {
      const retrospective: IRetrospective = { id: 456 };
      const sprint: ISprint = { id: 18234 };
      retrospective.sprint = sprint;

      const sprintCollection: ISprint[] = [{ id: 14167 }];
      jest.spyOn(sprintService, 'query').mockReturnValue(of(new HttpResponse({ body: sprintCollection })));
      const additionalSprints = [sprint];
      const expectedCollection: ISprint[] = [...additionalSprints, ...sprintCollection];
      jest.spyOn(sprintService, 'addSprintToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ retrospective });
      comp.ngOnInit();

      expect(sprintService.query).toHaveBeenCalled();
      expect(sprintService.addSprintToCollectionIfMissing).toHaveBeenCalledWith(
        sprintCollection,
        ...additionalSprints.map(expect.objectContaining),
      );
      expect(comp.sprintsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const retrospective: IRetrospective = { id: 456 };
      const sprint: ISprint = { id: 28259 };
      retrospective.sprint = sprint;

      activatedRoute.data = of({ retrospective });
      comp.ngOnInit();

      expect(comp.sprintsSharedCollection).toContain(sprint);
      expect(comp.retrospective).toEqual(retrospective);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRetrospective>>();
      const retrospective = { id: 123 };
      jest.spyOn(retrospectiveFormService, 'getRetrospective').mockReturnValue(retrospective);
      jest.spyOn(retrospectiveService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ retrospective });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: retrospective }));
      saveSubject.complete();

      // THEN
      expect(retrospectiveFormService.getRetrospective).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(retrospectiveService.update).toHaveBeenCalledWith(expect.objectContaining(retrospective));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRetrospective>>();
      const retrospective = { id: 123 };
      jest.spyOn(retrospectiveFormService, 'getRetrospective').mockReturnValue({ id: null });
      jest.spyOn(retrospectiveService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ retrospective: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: retrospective }));
      saveSubject.complete();

      // THEN
      expect(retrospectiveFormService.getRetrospective).toHaveBeenCalled();
      expect(retrospectiveService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRetrospective>>();
      const retrospective = { id: 123 };
      jest.spyOn(retrospectiveService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ retrospective });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(retrospectiveService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSprint', () => {
      it('Should forward to sprintService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(sprintService, 'compareSprint');
        comp.compareSprint(entity, entity2);
        expect(sprintService.compareSprint).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
