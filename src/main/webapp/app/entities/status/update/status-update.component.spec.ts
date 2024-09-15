import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { IStatusCategory } from 'app/entities/status-category/status-category.model';
import { StatusCategoryService } from 'app/entities/status-category/service/status-category.service';
import { StatusService } from '../service/status.service';
import { IStatus } from '../status.model';
import { StatusFormService } from './status-form.service';

import { StatusUpdateComponent } from './status-update.component';

describe('Status Management Update Component', () => {
  let comp: StatusUpdateComponent;
  let fixture: ComponentFixture<StatusUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let statusFormService: StatusFormService;
  let statusService: StatusService;
  let statusCategoryService: StatusCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StatusUpdateComponent],
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
      .overrideTemplate(StatusUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StatusUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    statusFormService = TestBed.inject(StatusFormService);
    statusService = TestBed.inject(StatusService);
    statusCategoryService = TestBed.inject(StatusCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call StatusCategory query and add missing value', () => {
      const status: IStatus = { id: 456 };
      const category: IStatusCategory = { id: 28126 };
      status.category = category;

      const statusCategoryCollection: IStatusCategory[] = [{ id: 10972 }];
      jest.spyOn(statusCategoryService, 'query').mockReturnValue(of(new HttpResponse({ body: statusCategoryCollection })));
      const additionalStatusCategories = [category];
      const expectedCollection: IStatusCategory[] = [...additionalStatusCategories, ...statusCategoryCollection];
      jest.spyOn(statusCategoryService, 'addStatusCategoryToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ status });
      comp.ngOnInit();

      expect(statusCategoryService.query).toHaveBeenCalled();
      expect(statusCategoryService.addStatusCategoryToCollectionIfMissing).toHaveBeenCalledWith(
        statusCategoryCollection,
        ...additionalStatusCategories.map(expect.objectContaining),
      );
      expect(comp.statusCategoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const status: IStatus = { id: 456 };
      const category: IStatusCategory = { id: 20567 };
      status.category = category;

      activatedRoute.data = of({ status });
      comp.ngOnInit();

      expect(comp.statusCategoriesSharedCollection).toContain(category);
      expect(comp.status).toEqual(status);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatus>>();
      const status = { id: 123 };
      jest.spyOn(statusFormService, 'getStatus').mockReturnValue(status);
      jest.spyOn(statusService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ status });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: status }));
      saveSubject.complete();

      // THEN
      expect(statusFormService.getStatus).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(statusService.update).toHaveBeenCalledWith(expect.objectContaining(status));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatus>>();
      const status = { id: 123 };
      jest.spyOn(statusFormService, 'getStatus').mockReturnValue({ id: null });
      jest.spyOn(statusService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ status: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: status }));
      saveSubject.complete();

      // THEN
      expect(statusFormService.getStatus).toHaveBeenCalled();
      expect(statusService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatus>>();
      const status = { id: 123 };
      jest.spyOn(statusService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ status });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(statusService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareStatusCategory', () => {
      it('Should forward to statusCategoryService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(statusCategoryService, 'compareStatusCategory');
        comp.compareStatusCategory(entity, entity2);
        expect(statusCategoryService.compareStatusCategory).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
