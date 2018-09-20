package cz.metacentrum.perun.core.impl;

import cz.metacentrum.perun.core.api.PerunPrincipal;

import java.util.concurrent.atomic.AtomicBoolean;

import cz.metacentrum.perun.core.api.*;
import cz.metacentrum.perun.core.api.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.metacentrum.perun.core.bl.PerunBl;

/**
 * Synchronizer, general tool for synchronization tasks.
 *
 * @author Michal Prochazka <michalp@ics.muni.cz>
 * @author Pavel Zlámal <zlamal@cesnet.cz>
 */
public class Synchronizer {

	private final static Logger log = LoggerFactory.getLogger(Synchronizer.class);
	private PerunSession sess;

	private PerunBl perunBl;
	private AtomicBoolean synchronizeGroupsRunning = new AtomicBoolean(false);
	private AtomicBoolean synchronizeExtSourcesRunning = new AtomicBoolean(false);
	private AtomicBoolean synchronizeUsersRunning = new AtomicBoolean(false);

	public Synchronizer() {
	}

	public Synchronizer(PerunBl perunBl) throws InternalErrorException {
		this.perunBl = perunBl;
		initialize();
	}

	/**
	 * Start synchronization of groups if not running.
	 *
	 * Method is triggered by Spring scheduler (every 5 minutes).
	 */
	public void synchronizeGroups() {
		if(perunBl.isPerunReadOnly()) {
			log.debug("This instance is just read only so skip synchronization of groups.");
			return;
		}

		if (synchronizeGroupsRunning.compareAndSet(false, true)) {
			try {
				log.debug("Synchronizer starting synchronizing the groups");
				this.perunBl.getGroupsManagerBl().synchronizeGroups(this.sess);
				if (!synchronizeGroupsRunning.compareAndSet(true, false)) {
					log.error("Synchronizer: group synchronization out of sync, resetting.");
					synchronizeGroupsRunning.set(false);
				}
			} catch (InternalErrorException e) {
				log.error("Cannot synchronize groups:", e);
				synchronizeGroupsRunning.set(false);
			}
		} else {
			log.debug("Synchronizer: group synchronization currently running.");
		}
	}

	/**
	 * Start synchronization of ExtSources if not running.
	 *
	 * Method is triggered by Spring scheduler (every 5 minutes).
	 */
	public void synchronizeExtSources() {
		if(perunBl.isPerunReadOnly()) {
			log.debug("This instance is just read only so skip synchronization of extSources.");
			return;
		}

		if (synchronizeExtSourcesRunning.compareAndSet(false, true)) {
			try {
				log.debug("Synchronizer starting synchronizing the extSources");
				this.perunBl.getExtSourcesManagerBl().synchronizeExtSources(this.sess);
				if (!synchronizeExtSourcesRunning.compareAndSet(true, false)) {
					log.error("Synchronizer: extSource synchronization out of sync, resetting.");
					synchronizeExtSourcesRunning.set(false);
				}
			} catch (InternalErrorException e) {
				log.error("Cannot synchronize extSources:", e);
				synchronizeExtSourcesRunning.set(false);
			}
		} else {
			log.debug("Synchronizer: extSources synchronization currently running.");
		}
	}

	/**
	 * Start synchronization of Users if not running.
	 *
	 * Method is triggered by Spring scheduler (every 5 minutes).
	 */
	public void synchronizeUsers() {
		if(perunBl.isPerunReadOnly()) {
			log.debug("This instance is just read only so skip synchronization of users.");
			return;
		}

		if (synchronizeUsersRunning.compareAndSet(false, true)) {
			try {
				log.debug("Synchronizer starting synchronizing the users");
				this.perunBl.getUsersManagerBl().reinitializeUserSynchronizerThreads(this.sess);
				if (!synchronizeUsersRunning.compareAndSet(true, false)) {
					log.error("Synchronizer: user synchronization out of sync, resetting.");
					synchronizeUsersRunning.set(false);
				}
			} catch (InternalErrorException e) {
				log.error("Cannot synchronize user:", e);
				synchronizeUsersRunning.set(false);
			}
		} else {
			log.debug("Synchronizer: user synchronization currently running.");
		}
	}

	public void removeAllExpiredBans() {
		if(perunBl.isPerunReadOnly()) {
			log.debug("This instance is just read only so skip removing expired bans.");
			return;
		}

		try {
			getPerun().getResourcesManagerBl().removeAllExpiredBansOnResources(sess);
			getPerun().getFacilitiesManagerBl().removeAllExpiredBansOnFacilities(sess);
		} catch (InternalErrorException ex) {
			log.error("Synchronizer: removeAllExpiredBans, exception {}", ex);
		}
	}

	public void initialize() throws InternalErrorException {
		String synchronizerPrincipal = "perunSynchronizer";
		this.sess = perunBl.getPerunSession(
				new PerunPrincipal(synchronizerPrincipal, ExtSourcesManager.EXTSOURCE_NAME_INTERNAL, ExtSourcesManager.EXTSOURCE_INTERNAL),
				new PerunClient());
	}

	public PerunBl getPerun() {
		return perunBl;
	}

	public void setPerun(PerunBl perunBl) {
		this.perunBl = perunBl;
	}

}
