package io.oasp.gastronomy.restaurant.offermanagement.dataaccess.impl.dao;

import static com.querydsl.core.alias.Alias.$;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import com.querydsl.core.alias.Alias;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;

import io.oasp.gastronomy.restaurant.general.common.api.datatype.Money;
import io.oasp.gastronomy.restaurant.general.dataaccess.base.dao.ApplicationMasterDataDaoImpl;
import io.oasp.gastronomy.restaurant.offermanagement.common.api.datatype.OfferSortByHitEntry;
import io.oasp.gastronomy.restaurant.offermanagement.common.api.datatype.OfferState;
import io.oasp.gastronomy.restaurant.offermanagement.dataaccess.api.DrinkEntity;
import io.oasp.gastronomy.restaurant.offermanagement.dataaccess.api.MealEntity;
import io.oasp.gastronomy.restaurant.offermanagement.dataaccess.api.OfferEntity;
import io.oasp.gastronomy.restaurant.offermanagement.dataaccess.api.SideDishEntity;
import io.oasp.gastronomy.restaurant.offermanagement.dataaccess.api.dao.OfferDao;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.OfferFilter;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.OfferSearchCriteriaTo;
import io.oasp.gastronomy.restaurant.offermanagement.logic.api.to.OfferSortBy;
import io.oasp.module.jpa.common.api.to.PaginatedListTo;
import io.oasp.module.jpa.common.base.LegacyDaoQuerySupport;

/**
 * Implementation of {@link OfferDao}.
 *
 */
@Named
public class OfferDaoImpl extends ApplicationMasterDataDaoImpl<OfferEntity> implements OfferDao {

  /**
   * The constructor.
   */
  public OfferDaoImpl() {

    super();
  }

  @Override
  public Class<OfferEntity> getEntityClass() {

    return OfferEntity.class;
  }

  @Override
  @Deprecated
  public List<OfferEntity> findOffersFiltered(OfferFilter offerFilterBo, OfferSortBy sortBy) {

    /*
     * Default error handling
     */
    if (offerFilterBo == null || sortBy == null) {
      return new ArrayList<>(0);
    }

    OfferEntity offer = Alias.alias(OfferEntity.class);
    JPQLQuery<OfferEntity> query = new JPAQuery<OfferEntity>(getEntityManager()).from($(offer));

    /*
     * Applying the filters
     */
    // Meal
    MealEntity meal = Alias.alias(MealEntity.class);
    if (offerFilterBo.getMealId() != null && offerFilterBo.getMealId() > 0) {
      query = query.join($(offer.getMeal()), $(meal));
      query.where($(meal.getId()).eq(offerFilterBo.getMealId()));
    }

    // Drink
    DrinkEntity drink = Alias.alias(DrinkEntity.class);
    if (offerFilterBo.getDrinkId() != null && offerFilterBo.getDrinkId() > 0) {
      query.join($(offer.getDrink()), $(drink));
      query.where($(drink.getId()).eq(offerFilterBo.getDrinkId()));
    }

    // SideDish
    SideDishEntity sideDish = Alias.alias(SideDishEntity.class);
    if (offerFilterBo.getSideDishId() != null && offerFilterBo.getSideDishId() > 0) {
      query.join($(offer.getSideDish()), $(sideDish));
      query.where($(sideDish.getId()).eq(offerFilterBo.getSideDishId()));
    }

    // only min price is given
    if (offerFilterBo.getMinPrice() != null) {
      query.where($(offer.getPrice()).goe(offerFilterBo.getMinPrice()));
    }

    // only max price is given
    if (offerFilterBo.getMaxPrice() != null) {
      query.where($(offer.getPrice()).loe(offerFilterBo.getMaxPrice()));
    }

    // sorting
    if (sortBy.getSortByEntry().equals(OfferSortByHitEntry.DESCRIPTION)) {
      if (sortBy.getOrderBy().isDesc())
        query.orderBy($(offer.getDescription()).desc());
      else
        query.orderBy($(offer.getDescription()).asc());
    } else if (sortBy.getSortByEntry().equals(OfferSortByHitEntry.PRICE)) {
      if (sortBy.getOrderBy().isDesc())
        query.orderBy($(offer.getPrice()).desc());
      else
        query.orderBy($(offer.getPrice()).asc());
    } else if (sortBy.getSortByEntry().equals(OfferSortByHitEntry.MEAL)) {
      if (sortBy.getOrderBy().isDesc())
        query.orderBy($(offer.getMeal().getDescription()).desc());
      else
        query.orderBy($(offer.getMeal().getDescription()).asc());
    } else if (sortBy.getSortByEntry().equals(OfferSortByHitEntry.DRINK)) {
      if (sortBy.getOrderBy().isDesc())
        query.orderBy($(offer.getDrink().getDescription()).desc());
      else
        query.orderBy($(offer.getDrink().getDescription()).asc());
    } else if (sortBy.getSortByEntry().equals(OfferSortByHitEntry.SIDEDISH)) {
      if (sortBy.getOrderBy().isDesc())
        query.orderBy($(offer.getSideDish().getDescription()).desc());
      else
        query.orderBy($(offer.getSideDish().getDescription()).asc());
    } else {
      if (sortBy.getOrderBy().isDesc())
        query.orderBy($(offer.getId()).desc());
      else
        query.orderBy($(offer.getId()).asc());
    }

    /*
     * Result
     */
    List<OfferEntity> result = query.fetch();
    return result;
  }

  @Override
  public PaginatedListTo<OfferEntity> findOffers(OfferSearchCriteriaTo criteria) {

    OfferEntity offer = Alias.alias(OfferEntity.class);
    EntityPathBase<OfferEntity> alias = $(offer);
    JPAQuery<OfferEntity> query = new JPAQuery<OfferEntity>(getEntityManager()).from(alias);

    Long number = criteria.getNumber();
    if (number != null) {
      query.where($(offer.getNumber()).eq(number));
    }
    Long mealId = criteria.getMealId();
    if (mealId != null) {
      query.where($(offer.getMealId()).eq(mealId));
    }
    Long drinkId = criteria.getDrinkId();
    if (drinkId != null) {
      query.where($(offer.getDrinkId()).eq(drinkId));
    }
    Long sideDishId = criteria.getSideDishId();
    if (sideDishId != null) {
      query.where($(offer.getSideDishId()).eq(sideDishId));
    }
    OfferState state = criteria.getState();
    if (state != null) {
      query.where($(offer.getState()).eq(state));
    }

    Money minPrice = criteria.getMinPrice();
    if (minPrice != null) {
      query.where($(offer.getPrice()).goe(minPrice));
    }

    Money maxPrice = criteria.getMaxPrice();
    if (maxPrice != null) {
      query.where($(offer.getPrice()).loe(maxPrice));
    }

    return LegacyDaoQuerySupport.findPaginated(criteria, query);
  }
}
