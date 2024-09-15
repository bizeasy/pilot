import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IParty } from 'app/entities/party/party.model';
import { PartyService } from 'app/entities/party/service/party.service';
import { ISprint } from 'app/entities/sprint/sprint.model';
import { SprintService } from 'app/entities/sprint/service/sprint.service';
import { ITaskHistory } from '../task-history.model';
import { TaskHistoryService } from '../service/task-history.service';
import { TaskHistoryFormService } from './task-history-form.service';

import { TaskHistoryUpdateComponent } from './task-history-update.component';

describe('TaskHistory Management Update Component', () => {
  let comp: TaskHistoryUpdateComponent;
  let fixture: ComponentFixture<TaskHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskHistoryFormService: TaskHistoryFormService;
  let taskHistoryService: TaskHistoryService;
  let partyService: PartyService;
  let sprintService: SprintService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TaskHistoryUpdateComponent],
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
      .overrideTemplate(TaskHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskHistoryFormService = TestBed.inject(TaskHistoryFormService);
    taskHistoryService = TestBed.inject(TaskHistoryService);
    partyService = TestBed.inject(PartyService);
    sprintService = TestBed.inject(SprintService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Party query and add missing value', () => {
      const taskHistory: ITaskHistory = { id: 456 };
      const assignedTo: IParty = { id: 25045 };
      taskHistory.assignedTo = assignedTo;
      const assignedBy: IParty = { id: 10602 };
      taskHistory.assignedBy = assignedBy;

      const partyCollection: IParty[] = [{ id: 13650 }];
      jest.spyOn(partyService, 'query').mockReturnValue(of(new HttpResponse({ body: partyCollection })));
      const additionalParties = [assignedTo, assignedBy];
      const expectedCollection: IParty[] = [...additionalParties, ...partyCollection];
      jest.spyOn(partyService, 'addPartyToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ taskHistory });
      comp.ngOnInit();

      expect(partyService.query).toHaveBeenCalled();
      expect(partyService.addPartyToCollectionIfMissing).toHaveBeenCalledWith(
        partyCollection,
        ...additionalParties.map(expect.objectContaining),
      );
      expect(comp.partiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Sprint query and add missing value', () => {
      const taskHistory: ITaskHistory = { id: 456 };
      const sprint: ISprint = { id: 7629 };
      taskHistory.sprint = sprint;

      const sprintCollection: ISprint[] = [{ id: 963 }];
      jest.spyOn(sprintService, 'query').mockReturnValue(of(new HttpResponse({ body: sprintCollection })));
      const additionalSprints = [sprint];
      const expectedCollection: ISprint[] = [...additionalSprints, ...sprintCollection];
      jest.spyOn(sprintService, 'addSprintToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ taskHistory });
      comp.ngOnInit();

      expect(sprintService.query).toHaveBeenCalled();
      expect(sprintService.addSprintToCollectionIfMissing).toHaveBeenCalledWith(
        sprintCollection,
        ...additionalSprints.map(expect.objectContaining),
      );
      expect(comp.sprintsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const taskHistory: ITaskHistory = { id: 456 };
      const assignedTo: IParty = { id: 13933 };
      taskHistory.assignedTo = assignedTo;
      const assignedBy: IParty = { id: 32715 };
      taskHistory.assignedBy = assignedBy;
      const sprint: ISprint = { id: 18735 };
      taskHistory.sprint = sprint;

      activatedRoute.data = of({ taskHistory });
      comp.ngOnInit();

      expect(comp.partiesSharedCollection).toContain(assignedTo);
      expect(comp.partiesSharedCollection).toContain(assignedBy);
      expect(comp.sprintsSharedCollection).toContain(sprint);
      expect(comp.taskHistory).toEqual(taskHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskHistory>>();
      const taskHistory = { id: 123 };
      jest.spyOn(taskHistoryFormService, 'getTaskHistory').mockReturnValue(taskHistory);
      jest.spyOn(taskHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskHistory }));
      saveSubject.complete();

      // THEN
      expect(taskHistoryFormService.getTaskHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(taskHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskHistory>>();
      const taskHistory = { id: 123 };
      jest.spyOn(taskHistoryFormService, 'getTaskHistory').mockReturnValue({ id: null });
      jest.spyOn(taskHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskHistory }));
      saveSubject.complete();

      // THEN
      expect(taskHistoryFormService.getTaskHistory).toHaveBeenCalled();
      expect(taskHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskHistory>>();
      const taskHistory = { id: 123 };
      jest.spyOn(taskHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareParty', () => {
      it('Should forward to partyService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(partyService, 'compareParty');
        comp.compareParty(entity, entity2);
        expect(partyService.compareParty).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
