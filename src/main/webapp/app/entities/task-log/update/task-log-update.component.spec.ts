import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { TaskLogService } from '../service/task-log.service';
import { ITaskLog } from '../task-log.model';
import { TaskLogFormService } from './task-log-form.service';

import { TaskLogUpdateComponent } from './task-log-update.component';

describe('TaskLog Management Update Component', () => {
  let comp: TaskLogUpdateComponent;
  let fixture: ComponentFixture<TaskLogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let taskLogFormService: TaskLogFormService;
  let taskLogService: TaskLogService;
  let taskService: TaskService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TaskLogUpdateComponent],
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
      .overrideTemplate(TaskLogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TaskLogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    taskLogFormService = TestBed.inject(TaskLogFormService);
    taskLogService = TestBed.inject(TaskLogService);
    taskService = TestBed.inject(TaskService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Task query and add missing value', () => {
      const taskLog: ITaskLog = { id: 456 };
      const task: ITask = { id: 4869 };
      taskLog.task = task;

      const taskCollection: ITask[] = [{ id: 25494 }];
      jest.spyOn(taskService, 'query').mockReturnValue(of(new HttpResponse({ body: taskCollection })));
      const additionalTasks = [task];
      const expectedCollection: ITask[] = [...additionalTasks, ...taskCollection];
      jest.spyOn(taskService, 'addTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ taskLog });
      comp.ngOnInit();

      expect(taskService.query).toHaveBeenCalled();
      expect(taskService.addTaskToCollectionIfMissing).toHaveBeenCalledWith(
        taskCollection,
        ...additionalTasks.map(expect.objectContaining),
      );
      expect(comp.tasksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const taskLog: ITaskLog = { id: 456 };
      const task: ITask = { id: 27282 };
      taskLog.task = task;

      activatedRoute.data = of({ taskLog });
      comp.ngOnInit();

      expect(comp.tasksSharedCollection).toContain(task);
      expect(comp.taskLog).toEqual(taskLog);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskLog>>();
      const taskLog = { id: 123 };
      jest.spyOn(taskLogFormService, 'getTaskLog').mockReturnValue(taskLog);
      jest.spyOn(taskLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskLog }));
      saveSubject.complete();

      // THEN
      expect(taskLogFormService.getTaskLog).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(taskLogService.update).toHaveBeenCalledWith(expect.objectContaining(taskLog));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskLog>>();
      const taskLog = { id: 123 };
      jest.spyOn(taskLogFormService, 'getTaskLog').mockReturnValue({ id: null });
      jest.spyOn(taskLogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskLog: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: taskLog }));
      saveSubject.complete();

      // THEN
      expect(taskLogFormService.getTaskLog).toHaveBeenCalled();
      expect(taskLogService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITaskLog>>();
      const taskLog = { id: 123 };
      jest.spyOn(taskLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taskLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(taskLogService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTask', () => {
      it('Should forward to taskService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(taskService, 'compareTask');
        comp.compareTask(entity, entity2);
        expect(taskService.compareTask).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
