import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStatus } from 'app/entities/status/status.model';
import { StatusService } from 'app/entities/status/service/status.service';
import { SprintTaskHistoryService } from '../service/sprint-task-history.service';
import { ISprintTaskHistory } from '../sprint-task-history.model';
import { SprintTaskHistoryFormService } from './sprint-task-history-form.service';

import { SprintTaskHistoryUpdateComponent } from './sprint-task-history-update.component';

describe('SprintTaskHistory Management Update Component', () => {
  let comp: SprintTaskHistoryUpdateComponent;
  let fixture: ComponentFixture<SprintTaskHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sprintTaskHistoryFormService: SprintTaskHistoryFormService;
  let sprintTaskHistoryService: SprintTaskHistoryService;
  let statusService: StatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [SprintTaskHistoryUpdateComponent],
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
      .overrideTemplate(SprintTaskHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SprintTaskHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sprintTaskHistoryFormService = TestBed.inject(SprintTaskHistoryFormService);
    sprintTaskHistoryService = TestBed.inject(SprintTaskHistoryService);
    statusService = TestBed.inject(StatusService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Status query and add missing value', () => {
      const sprintTaskHistory: ISprintTaskHistory = { id: 456 };
      const fromStatus: IStatus = { id: 15897 };
      sprintTaskHistory.fromStatus = fromStatus;
      const toStatus: IStatus = { id: 15140 };
      sprintTaskHistory.toStatus = toStatus;

      const statusCollection: IStatus[] = [{ id: 27881 }];
      jest.spyOn(statusService, 'query').mockReturnValue(of(new HttpResponse({ body: statusCollection })));
      const additionalStatuses = [fromStatus, toStatus];
      const expectedCollection: IStatus[] = [...additionalStatuses, ...statusCollection];
      jest.spyOn(statusService, 'addStatusToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sprintTaskHistory });
      comp.ngOnInit();

      expect(statusService.query).toHaveBeenCalled();
      expect(statusService.addStatusToCollectionIfMissing).toHaveBeenCalledWith(
        statusCollection,
        ...additionalStatuses.map(expect.objectContaining),
      );
      expect(comp.statusesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sprintTaskHistory: ISprintTaskHistory = { id: 456 };
      const fromStatus: IStatus = { id: 16952 };
      sprintTaskHistory.fromStatus = fromStatus;
      const toStatus: IStatus = { id: 27819 };
      sprintTaskHistory.toStatus = toStatus;

      activatedRoute.data = of({ sprintTaskHistory });
      comp.ngOnInit();

      expect(comp.statusesSharedCollection).toContain(fromStatus);
      expect(comp.statusesSharedCollection).toContain(toStatus);
      expect(comp.sprintTaskHistory).toEqual(sprintTaskHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintTaskHistory>>();
      const sprintTaskHistory = { id: 123 };
      jest.spyOn(sprintTaskHistoryFormService, 'getSprintTaskHistory').mockReturnValue(sprintTaskHistory);
      jest.spyOn(sprintTaskHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintTaskHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sprintTaskHistory }));
      saveSubject.complete();

      // THEN
      expect(sprintTaskHistoryFormService.getSprintTaskHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sprintTaskHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(sprintTaskHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintTaskHistory>>();
      const sprintTaskHistory = { id: 123 };
      jest.spyOn(sprintTaskHistoryFormService, 'getSprintTaskHistory').mockReturnValue({ id: null });
      jest.spyOn(sprintTaskHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintTaskHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sprintTaskHistory }));
      saveSubject.complete();

      // THEN
      expect(sprintTaskHistoryFormService.getSprintTaskHistory).toHaveBeenCalled();
      expect(sprintTaskHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISprintTaskHistory>>();
      const sprintTaskHistory = { id: 123 };
      jest.spyOn(sprintTaskHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sprintTaskHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sprintTaskHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStatus', () => {
      it('Should forward to statusService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(statusService, 'compareStatus');
        comp.compareStatus(entity, entity2);
        expect(statusService.compareStatus).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
