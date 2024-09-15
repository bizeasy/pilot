import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IParty } from '../party.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../party.test-samples';

import { PartyService, RestParty } from './party.service';

const requireRestSample: RestParty = {
  ...sampleWithRequiredData,
  dob: sampleWithRequiredData.dob?.format(DATE_FORMAT),
};

describe('Party Service', () => {
  let service: PartyService;
  let httpMock: HttpTestingController;
  let expectedResult: IParty | IParty[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PartyService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Party', () => {
      const party = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(party).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Party', () => {
      const party = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(party).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Party', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Party', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Party', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPartyToCollectionIfMissing', () => {
      it('should add a Party to an empty array', () => {
        const party: IParty = sampleWithRequiredData;
        expectedResult = service.addPartyToCollectionIfMissing([], party);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(party);
      });

      it('should not add a Party to an array that contains it', () => {
        const party: IParty = sampleWithRequiredData;
        const partyCollection: IParty[] = [
          {
            ...party,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPartyToCollectionIfMissing(partyCollection, party);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Party to an array that doesn't contain it", () => {
        const party: IParty = sampleWithRequiredData;
        const partyCollection: IParty[] = [sampleWithPartialData];
        expectedResult = service.addPartyToCollectionIfMissing(partyCollection, party);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(party);
      });

      it('should add only unique Party to an array', () => {
        const partyArray: IParty[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const partyCollection: IParty[] = [sampleWithRequiredData];
        expectedResult = service.addPartyToCollectionIfMissing(partyCollection, ...partyArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const party: IParty = sampleWithRequiredData;
        const party2: IParty = sampleWithPartialData;
        expectedResult = service.addPartyToCollectionIfMissing([], party, party2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(party);
        expect(expectedResult).toContain(party2);
      });

      it('should accept null and undefined values', () => {
        const party: IParty = sampleWithRequiredData;
        expectedResult = service.addPartyToCollectionIfMissing([], null, party, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(party);
      });

      it('should return initial array if no Party is added', () => {
        const partyCollection: IParty[] = [sampleWithRequiredData];
        expectedResult = service.addPartyToCollectionIfMissing(partyCollection, undefined, null);
        expect(expectedResult).toEqual(partyCollection);
      });
    });

    describe('compareParty', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareParty(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareParty(entity1, entity2);
        const compareResult2 = service.compareParty(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareParty(entity1, entity2);
        const compareResult2 = service.compareParty(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareParty(entity1, entity2);
        const compareResult2 = service.compareParty(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
