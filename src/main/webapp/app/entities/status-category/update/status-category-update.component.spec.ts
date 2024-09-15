import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { StatusCategoryService } from '../service/status-category.service';
import { IStatusCategory } from '../status-category.model';
import { StatusCategoryFormService } from './status-category-form.service';

import { StatusCategoryUpdateComponent } from './status-category-update.component';

describe('StatusCategory Management Update Component', () => {
  let comp: StatusCategoryUpdateComponent;
  let fixture: ComponentFixture<StatusCategoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let statusCategoryFormService: StatusCategoryFormService;
  let statusCategoryService: StatusCategoryService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [StatusCategoryUpdateComponent],
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
      .overrideTemplate(StatusCategoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StatusCategoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    statusCategoryFormService = TestBed.inject(StatusCategoryFormService);
    statusCategoryService = TestBed.inject(StatusCategoryService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const statusCategory: IStatusCategory = { id: 456 };

      activatedRoute.data = of({ statusCategory });
      comp.ngOnInit();

      expect(comp.statusCategory).toEqual(statusCategory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatusCategory>>();
      const statusCategory = { id: 123 };
      jest.spyOn(statusCategoryFormService, 'getStatusCategory').mockReturnValue(statusCategory);
      jest.spyOn(statusCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ statusCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: statusCategory }));
      saveSubject.complete();

      // THEN
      expect(statusCategoryFormService.getStatusCategory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(statusCategoryService.update).toHaveBeenCalledWith(expect.objectContaining(statusCategory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatusCategory>>();
      const statusCategory = { id: 123 };
      jest.spyOn(statusCategoryFormService, 'getStatusCategory').mockReturnValue({ id: null });
      jest.spyOn(statusCategoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ statusCategory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: statusCategory }));
      saveSubject.complete();

      // THEN
      expect(statusCategoryFormService.getStatusCategory).toHaveBeenCalled();
      expect(statusCategoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IStatusCategory>>();
      const statusCategory = { id: 123 };
      jest.spyOn(statusCategoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ statusCategory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(statusCategoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
